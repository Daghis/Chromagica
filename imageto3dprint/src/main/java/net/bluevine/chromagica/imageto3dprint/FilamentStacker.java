package net.bluevine.chromagica.imageto3dprint;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingDouble;
import static net.bluevine.chromagica.common.ColorUtil.getColorForSequence;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Value;
import net.bluevine.chromagica.common.model.FilamentData;
import net.bluevine.chromagica.common.model.RGBColor;

public class FilamentStacker {
  private final Map<String, FilamentData> filamentData;
  private final String baseFilament;
  private final int maxLayers;

  @Value
  @Builder
  public static class FilamentStack {
    List<String> filamentSequence;
    RGBColor color;
    double differenceFromTarget;
  }

  public FilamentStacker(
      Map<String, FilamentData> filamentData, String baseFilament, int maxLayers) {
    this.filamentData =
        ImmutableMap.copyOf(checkNotNull(filamentData, "filamentData cannot be null"));
    this.baseFilament = checkNotNull(baseFilament, "baseFilament cannot be null");
    checkArgument(maxLayers > 0, "maxLayers must be positive");
    this.maxLayers = maxLayers;
  }

  private static final int BEAM_WIDTH = 50;

  @Value
  @Builder
  private static class BeamEntry {
    RGBColor color;
    List<String> filamentSequence;
    double differenceFromTarget;
  }

  public FilamentStack optimizeColorSequence(RGBColor targetColor) {
    return optimizeColorSequence(targetColor, emptyList());
  }

  private TreeSet<BeamEntry> initializeBeam(RGBColor targetColor, List<String> topColors) {
    TreeSet<BeamEntry> beam = new TreeSet<>(comparingDouble(BeamEntry::getDifferenceFromTarget));
    // Start off the beam with 1 layer of each filament.
    for (Entry<String, FilamentData> filamentDataEntry : filamentData.entrySet()) {
      String filament = filamentDataEntry.getKey();
      if (!topColors.isEmpty() && !Iterables.getLast(topColors).equals(filament)) {
        continue;
      }

      List<String> sequence = ImmutableList.of(filamentDataEntry.getKey());
      RGBColor color = getCachedColorForSequence(sequence);
      beam.add(
          BeamEntry.builder()
              .color(color)
              .filamentSequence(sequence)
              .differenceFromTarget(color.computeLabDistance(targetColor))
              .build());
    }

    return beam;
  }

  public FilamentStack optimizeColorSequence(RGBColor targetColor, List<String> topColors) {
    TreeSet<BeamEntry> beam = initializeBeam(targetColor, topColors);

    int lastBeamSize = 0;
    while (lastBeamSize < beam.size()) {
      lastBeamSize = beam.size();

      // Find the best entries from the beam to work with.
      List<List<String>> sequenceCandidates =
          beam.stream()
              .map(BeamEntry::getFilamentSequence)
              // Ensure that candidates have room for one more filament in the sequence.
              .filter(sequence -> sequence.size() < maxLayers)
              .limit(BEAM_WIDTH)
              .collect(toImmutableList());

      for (List<String> sequence : sequenceCandidates) {
        for (String filament : filamentData.keySet()) {
          List<String> newSequence =
              Stream.concat(Stream.of(filament), sequence.stream()).collect(toImmutableList());

          RGBColor newColor = getCachedColorForSequence(newSequence);
          double difference = newColor.computeLabDistance(targetColor);
          beam.add(
              BeamEntry.builder()
                  .color(newColor)
                  .filamentSequence(newSequence)
                  .differenceFromTarget(difference)
                  .build());
        }
      }
    }

    BeamEntry best = checkNotNull(beam.first());
    List<String> bestSequence = best.getFilamentSequence();
    // Ensure that the result is padded to maxLayers.
    bestSequence =
        Stream.concat(
                Collections.nCopies(maxLayers - bestSequence.size(), baseFilament).stream(),
                bestSequence.stream())
            .collect(toImmutableList());

    return FilamentStack.builder()
        .filamentSequence(ImmutableList.copyOf(bestSequence))
        .color(best.getColor())
        .differenceFromTarget(best.getDifferenceFromTarget())
        .build();
  }

  private final Cache<List<String>, RGBColor> colorCache =
      CacheBuilder.newBuilder().maximumSize(1000).build();

  private RGBColor getCachedColorForSequence(List<String> sequence) {
    try {
      return colorCache.get(
          sequence, () -> getColorForSequence(sequence, filamentData, baseFilament));
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    }
  }
}
