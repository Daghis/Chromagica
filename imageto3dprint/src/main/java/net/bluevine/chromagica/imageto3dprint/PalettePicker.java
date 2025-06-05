package net.bluevine.chromagica.imageto3dprint;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparingDouble;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Data;
import me.tongfei.progressbar.ProgressBar;
import net.bluevine.chromagica.common.ColorUtil;
import net.bluevine.chromagica.common.model.FilamentData;
import net.bluevine.chromagica.common.model.RGBColor;

public class PalettePicker {

  ImmutableMap<String, FilamentData> filamentData;
  @Nullable final String baseFilament;
  final ImmutableSet<String> requiredFilaments;
  final ImmutableList<String> filamentKeys;
  int numConcurrentFilaments;
  int numLayersToCompute;
  BufferedImage image;

  private final Multiset<RGBColor> dominantColors;

  private static final int PALETTE_BEAM_SIZE = 250;
  private static final int IMAGE_CLUSTERS = 25;

  public PalettePicker(
      @Nonnull Map<String, FilamentData> filamentData,
      int numConcurrentFilaments,
      int numLayersToCompute,
      @Nullable String baseFilament,
      Collection<String> excludedFilaments,
      Collection<String> requiredFilaments,
      BufferedImage image) {
    this.baseFilament = baseFilament;

    if (excludedFilaments != null) {
      checkArgument(
          baseFilament == null || !excludedFilaments.contains(baseFilament),
          "Base filament %s cannot also be excluded",
          baseFilament);
      this.filamentData =
          requireNonNull(filamentData, "filamentData cannot be null").entrySet().stream()
              .filter(entry -> !excludedFilaments.contains(entry.getKey()))
              .collect(toImmutableMap(Entry::getKey, Entry::getValue));
    } else {
      this.filamentData = ImmutableMap.copyOf(filamentData);
    }

    filamentKeys = this.filamentData.keySet().stream().sorted().collect(toImmutableList());
    // Ensure baseFilament is a required filament.
    this.requiredFilaments =
        Stream.concat(Stream.ofNullable(baseFilament), requiredFilaments.stream())
            .collect(toImmutableSet());
    checkArgument(
        this.requiredFilaments.size() <= numConcurrentFilaments,
        "Too many required filaments for %d concurrent",
        numConcurrentFilaments);
    this.numConcurrentFilaments = numConcurrentFilaments;
    this.numLayersToCompute = numLayersToCompute;

    this.image = requireNonNull(image, "image cannot be null");
    List<RGBColor> pixelColors =
        Arrays.stream(
                image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()))
            .mapToObj(colorValues -> new Color(colorValues, image.getColorModel().hasAlpha()))
            .filter(color -> color.getAlpha() > 0)
            .map(RGBColor::new)
            .collect(toImmutableList());
    dominantColors = ColorUtil.getDominantColors(pixelColors, IMAGE_CLUSTERS);
  }

  @Data
  @SuppressWarnings("unused")
  public static class Palette {
    final String baseFilament;
    final Set<String> filaments;
    double differenceScore = Double.MAX_VALUE;
  }

  private static final Comparator<Palette> PALETTE_COMPARATOR =
      comparingDouble(Palette::getDifferenceScore)
          .thenComparing(
              palette -> String.join(",", new TreeSet<>(palette.getFilaments())));

  public ImmutableList<Palette> getBestPalettes(int countToReturn) {
    TreeSet<Palette> paletteBeam = new TreeSet<>(PALETTE_COMPARATOR);

    try (ProgressBar progressBar = new ProgressBar("Evaluating palettes", numConcurrentFilaments)) {
      filamentKeys.stream()
          .filter(filament -> requiredFilaments.isEmpty() || requiredFilaments.contains(filament))
          .filter(filament -> baseFilament == null || baseFilament.equals(filament))
          .map(
              filament ->
                  new Palette(
                      filament,
                      Stream.concat(Stream.of(filament), requiredFilaments.stream())
                          .collect(toImmutableSet())))
          .peek(palette -> palette.setDifferenceScore(evaluateOnePalette(palette)))
          .forEach(paletteBeam::add);
      progressBar.step();

      for (int i = 1; i < numConcurrentFilaments; i++) {
        int lengthFilter = i;

        paletteBeam.stream()
            .filter(palette -> palette.getFilaments().size() == lengthFilter)
            .limit(PALETTE_BEAM_SIZE)
            .parallel()
            .flatMap(
                palette ->
                    filamentKeys.stream()
                        .filter(filament -> !palette.getFilaments().contains(filament))
                        .map(
                            filament ->
                                new Palette(
                                    palette.getBaseFilament(),
                                    Stream.concat(
                                            Stream.of(filament), palette.getFilaments().stream())
                                        .collect(toImmutableSet()))))
            .peek(palette -> palette.setDifferenceScore(evaluateOnePalette(palette)))
            .forEach(paletteBeam::add);

        progressBar.step();
      }
    }

    return paletteBeam.stream()
        .filter(palette -> palette.getFilaments().size() == numConcurrentFilaments)
        .limit(countToReturn)
        .collect(toImmutableList());
  }

  private double evaluateOnePalette(Palette palette) {
    ImmutableMap<String, FilamentData> paletteData =
        filamentData.entrySet().stream()
            .filter(
                entry ->
                    palette.getBaseFilament().equals(entry.getKey())
                        || palette.getFilaments().contains(entry.getKey()))
            .collect(toImmutableMap(Entry::getKey, Entry::getValue));

    FilamentStacker stacker =
        new FilamentStacker(paletteData, palette.getBaseFilament(), numLayersToCompute);

    return Math.sqrt(
        dominantColors.entrySet().stream()
                .mapToDouble(
                    colorEntry -> {
                      RGBColor color = colorEntry.getElement();
                      double difference =
                          stacker.optimizeColorSequence(color).getDifferenceFromTarget();
                      return difference * difference * colorEntry.getCount();
                    })
                .sum()
            / dominantColors.size());
  }
}
