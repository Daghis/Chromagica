package net.bluevine.chromagica.imageto3dprint;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingDouble;
import static net.bluevine.chromagica.imageto3dprint.ImageUtil.resizeImage;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import lombok.Value;
import me.tongfei.progressbar.ProgressBar;
import net.bluevine.chromagica.Version;
import net.bluevine.chromagica.common.FilamentDataHandler;
import net.bluevine.chromagica.common.model.FilamentData;
import net.bluevine.chromagica.common.model.RGBColor;
import net.bluevine.chromagica.imageto3dprint.FilamentStacker.FilamentStack;
import net.bluevine.chromagica.imageto3dprint.PalettePicker.Palette;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Command(
    name = "ImageTo3dPrint",
    mixinStandardHelpOptions = true,
    version = Version.VERSION,
    description =
        "Converts an image to a set of interleaved, color-keyed 3D models to 3D-print the color image.")
public class ImageTo3dPrint implements Runnable {
  @Spec CommandSpec spec;

  @Parameters(index = "0", description = "The image file to convert to 3D-print files.")
  private Path imagePath;

  @Parameters(
      index = "1",
      arity = "0..1",
      description = "Prefix to use for output files (defaults to the image's filename).")
  private String outputPrefix;

  private void setOutputPrefixDefault() {
    String fileName = imagePath.getFileName().toString();
    int lastDotIndex = fileName.lastIndexOf('.');
    outputPrefix = lastDotIndex > 0 ? fileName.substring(0, lastDotIndex) : fileName;
  }

  @Option(
      names = {"-d", "--database"},
      description = "Path to filament database (default: ${DEFAULT-VALUE}).")
  private Path databasePath = Path.of("filament_database.json");

  @Option(
      names = {"-b", "--base"},
      description = "Name of filament to be used as the base for the print.")
  private String baseFilament;

  @Option(
      names = {"-r", "--required"},
      split = ",",
      description = "List of filament names that must be included in the palette.")
  private List<String> requiredFilaments;

  @Option(
      names = {"-x", "--exclude"},
      split = ",",
      description = "List of filament names that are excluded from the palette.")
  private List<String> excludedFilaments;

  @Option(
      names = {"-n", "--num_filaments"},
      description =
          "Number of filaments to use concurrently from the provided filament database (default: ${DEFAULT-VALUE}).")
  private int numFilaments = 4;

  @Option(
      names = {"-l", "--num_layers"},
      description =
          "Maximum number of layers permitted to form colors (default: ${DEFAULT-VALUE}).")
  private int maxLayers = 8;

  @Option(
      names = {"-t", "--layer_height"},
      description = "Layer height (in mm) to use in resulting 3D model (default ${DEFAULT-VALUE}).")
  private double layerHeight = 0.08;

  @Option(
      names = {"-u", "--base_height"},
      description = "Height (in mm) of base below multicolored layers (default ${DEFAULT-VALUE}).")
  private double baseHeight = 1.0;

  @Option(
      names = {"-f", "--full_base"},
      description =
          "The base of the 3D model will be the full rectangular size (ignores pixel transparency, default ${DEFAULT-VALUE}).")
  private boolean fullBase = false;

  @Option(
      names = {"-p", "--precompute_layers"},
      description =
          "Number of layers to precompute for palette efficiency (default: ${DEFAULT-VALUE}).")
  private int precomputeLayers = 4;

  @Option(
      names = {"-s", "--size", "--output_size"},
      description = "Size (in mm) of longest edge of 3D model (default: ${DEFAULT-VALUE}).")
  private double outputSize = 100.0;

  @Option(
      names = {"-z", "--pixel_size"},
      description = "Pixel size (in mm) when printed (default: ${DEFAULT-VALUE}).")
  private double pixelSize = 0.5;

  @Option(
      names = {"-o", "--best_of"},
      description =
          "The number of the best precomputed palettes to run full evaluations to find the optimal "
              + "color palette (default: ${DEFAULT-VALUE}).")
  private int bestOf = 5;

  Map<String, FilamentData> filamentData;

  private BufferedImage image;

  private static Map<String, FilamentData> getFilamentData(Path databasePath) {
    try {
      return FilamentDataHandler.readFromFile(databasePath);
    } catch (NoSuchFileException e) {
      System.out.printf("Filament database %s not found; starting new database.%n", databasePath);
      return new HashMap<>();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static List<String> listOfNullable(@Nullable List<String> list) {
    return list == null ? emptyList() : list;
  }

  @Value
  private static class FilamentResult {
    Palette palette;
    Table<Integer, Integer, List<String>> filamentStacks;
    double differenceScore;
    BufferedImage image;
  }

  private FilamentResult getFilamentResult(Palette palette) {
    Map<String, FilamentData> filteredFilamentData =
        filamentData.entrySet().stream()
            .filter(entry -> palette.getFilaments().contains(entry.getKey()))
            .collect(toImmutableMap(Entry::getKey, Entry::getValue));

    FilamentStacker stacker =
        new FilamentStacker(filteredFilamentData, palette.getBaseFilament(), maxLayers);
    Table<Integer, Integer, List<String>> filamentStacks = HashBasedTable.create();
    double squareDifferenceSum = 0;

    BufferedImage generatedImage =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        Color pixel = new Color(image.getRGB(x, y), image.getColorModel().hasAlpha());
        if (pixel.getAlpha() == 0) {
          // Skip transparent pixels. We don't output them at all.
          continue;
        }

        RGBColor pixelColor = new RGBColor(pixel);
        FilamentStack filamentStack = stacker.optimizeColorSequence(pixelColor);

        filamentStacks.put(x, y, filamentStack.getFilamentSequence());
        squareDifferenceSum +=
            filamentStack.getDifferenceFromTarget() * filamentStack.getDifferenceFromTarget();

        generatedImage.setRGB(x, y, new Color(filamentStack.getColor().getRGB()).getRGB());
      }
    }

    return new FilamentResult(
        palette,
        filamentStacks,
        Math.sqrt(squareDifferenceSum / filamentStacks.size()),
        generatedImage);
  }

  private List<FilamentResult> getFinalResults(Collection<Palette> palettes) {
    ConcurrentHashMultiset<FilamentResult> results = ConcurrentHashMultiset.create();
    try (ProgressBar progressBar = new ProgressBar("Determining final result", palettes.size())) {
      palettes.parallelStream()
          .map(this::getFilamentResult)
          .peek(x -> progressBar.step())
          .forEach(results::add);
    }

    ImmutableList<FilamentResult> sortedResults =
        results.stream()
            .sorted(comparingDouble(FilamentResult::getDifferenceScore))
            .collect(toImmutableList());

    sortedResults.forEach(
        result ->
            System.out.printf(
                "Originally %.2f, now %.2f (%s)%n",
                result.getPalette().getDifferenceScore(),
                result.getDifferenceScore(),
                result.getPalette().getFilaments()));

    return sortedResults;
  }

  @Override
  public void run() {
    if (outputPrefix == null) {
      setOutputPrefixDefault();
    }

    filamentData = getFilamentData(databasePath);

    try {
      image =
          resizeImage(
              checkNotNull(ImageIO.read(imagePath.toFile())), (int) (outputSize / pixelSize));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    PalettePicker picker =
        new PalettePicker(
            filamentData,
            numFilaments,
            precomputeLayers,
            baseFilament,
            listOfNullable(excludedFilaments),
            listOfNullable(requiredFilaments),
            image);
    ImmutableList<Palette> bestPalettes = picker.getBestPalettes(bestOf);
    List<FilamentResult> results = getFinalResults(bestPalettes);
    FilamentResult result = results.get(0);
    System.out.printf("Base filament: %s%n", result.getPalette().getBaseFilament());

    try {
      ImageIO.write(result.getImage(), "PNG", new File("EVAL.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    ScadGenerator scadGenerator =
        new ScadGenerator(pixelSize, layerHeight, baseHeight, result.getFilamentStacks());
    scadGenerator.generateScadContent().entrySet().parallelStream()
        .forEach(
            entry -> {
              try {
                Path outputPath = Path.of(String.format("%s-%s.stl", outputPrefix, entry.getKey()));
                StlGenerator.createStlFile(entry.getValue(), outputPath);
              } catch (IOException | InterruptedException e) {
                System.err.println(
                    "Error creating STL for " + entry.getKey() + ": " + e.getMessage());
                throw new RuntimeException("Failed to generate STL for key: " + entry.getKey(), e);
              }
            });
  }

  public static void main(String[] args) {
    System.exit(new CommandLine(new ImageTo3dPrint()).execute(args));
  }
}
