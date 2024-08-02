package net.bluevine.chromagica;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static net.bluevine.chromagica.ColorUtil.calculateDifference;
import static net.bluevine.chromagica.ColorUtil.getAverageColor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.google.common.flogger.FluentLogger;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import net.bluevine.chromagica.data.FilamentData;
import net.bluevine.chromagica.data.RGBCoefficients;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.jetbrains.annotations.NotNull;

public class ColorAnalyzer {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private int numColors;
  private List<String> filamentNames;

  private int numChipRows;
  private int numChipCols;

  private int numChipRowsPerColor;
  private int numChipColsPerColor;

  private Table<String, String, List<Color>> chipsPerColorSquare;
  private Map<String, Color> filamentColors;
  private Map<String, Map<Color, Color>> filamentColorMappings;
  private Map<String, RGBCoefficients> filamentCoefficients;
  public ImmutableMap<String, FilamentData> filamentData;

  private static final double CHIP_COLOR_VIGNETTE = 0.8;

  private static final char PLUS_SIGN = '+';
  private static final char MINUS_SIGN = '−';

  private ColorAnalyzer() {}

  private Color getAverageColorOfChip(@NotNull BufferedImage image, int x, int y) {
    double chipWidth = (double) image.getWidth() / numChipCols;
    double chipHeight = (double) image.getHeight() / numChipRows;

    int vignetteWidth = (int) Math.max(1, (chipWidth * CHIP_COLOR_VIGNETTE));
    int vignetteHeight = (int) Math.max(1, (chipHeight * CHIP_COLOR_VIGNETTE));

    double margin = (1.0 - CHIP_COLOR_VIGNETTE) / 2;
    int left = (int) ((x + margin) * chipWidth);
    int top = (int) ((y + margin) * chipHeight);

    // Get a list of RGB pixel values for the specified region
    List<Color> vignetteColors =
        Arrays.stream(
                image.getRGB(left, top, vignetteWidth, vignetteHeight, null, 0, vignetteWidth))
            .mapToObj(Color::new)
            .collect(toImmutableList());

    return getAverageColor(vignetteColors);
  }

  private void loadChipColors(BufferedImage image) {
    ImmutableTable.Builder<String, String, List<Color>> perSquareBuilder = ImmutableTable.builder();
    for (int colorY = 0; colorY < numColors; colorY++) {
      for (int colorX = 0; colorX < numColors; colorX++) {
        ImmutableList.Builder<Color> colorsBuilder = ImmutableList.builder();
        for (int chipY = colorY * numChipRowsPerColor;
            chipY < (colorY + 1) * numChipRowsPerColor;
            chipY++) {
          for (int chipX = colorX * numChipColsPerColor;
              chipX < (colorX + 1) * numChipColsPerColor;
              chipX++) {
            Color color = getAverageColorOfChip(image, chipX, chipY);
            colorsBuilder.add(color);
          }
        }
        perSquareBuilder.put(
            filamentNames.get(colorY), filamentNames.get(colorX), colorsBuilder.build());
      }
    }

    chipsPerColorSquare = perSquareBuilder.build();
  }

  private void findUniformColorGrids() {
    enum Diagonal {
      LEFT,
      RIGHT
    }

    Map<Diagonal, Map<String, Color>> colorDiagonals = new HashMap<>();
    Map<Diagonal, Double> differences = new HashMap<>();

    for (Diagonal direction : ImmutableList.of(Diagonal.LEFT, Diagonal.RIGHT)) {
      Map<String, Pair<Color, Double>> diagonal = new HashMap<>();

      for (int i = 0; i < numColors; i++) {
        int columnForRow = direction == Diagonal.RIGHT ? i : numChipColsPerColor - i - 1;

        List<Color> diagonalChips =
            chipsPerColorSquare.get(filamentNames.get(i), filamentNames.get(columnForRow));
        checkNotNull(diagonalChips);

        Color overallAverageColor = getAverageColor(diagonalChips);
        double maxDistanceFromAverage =
            diagonalChips.stream()
                .map(color -> calculateDifference(color, overallAverageColor))
                .max(Double::compare)
                .orElse(0.0);
        diagonal.put(filamentNames.get(i), Pair.of(overallAverageColor, maxDistanceFromAverage));
      }

      Map<String, Color> colorsAlongDiagonal =
          diagonal.entrySet().stream()
              .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().getKey()));
      colorDiagonals.put(direction, colorsAlongDiagonal);

      double distances = diagonal.values().stream().mapToDouble(Pair::getValue).sum();
      differences.put(direction, distances);
    }

    if (differences.get(Diagonal.LEFT) < differences.get(Diagonal.RIGHT)) {
      filamentColors = colorDiagonals.get(Diagonal.LEFT);
    } else {
      filamentColors = colorDiagonals.get(Diagonal.RIGHT);
    }
  }

  private String formatCoefficient(double value) {
    return String.format("%c %.5f", value < 0 ? MINUS_SIGN : PLUS_SIGN, Math.abs(value));
  }

  private void getCoefficients() {
    if (numColors == 1) {
      logger.atWarning().log("Skipping coefficients due to only 1 color in image.");
      return;
    }

    filamentColorMappings = new HashMap<>();
    filamentCoefficients = new HashMap<>();

    for (Entry<String, Color> addedFilament : filamentColors.entrySet()) {
      String addedColor = addedFilament.getKey();

      Map<Color, Color> mappings = new HashMap<>();
      Map<Color, Color> existingMappings = filamentColorMappings.get(addedColor);
      if (existingMappings != null) {
        mappings.putAll(existingMappings);
      }

      WeightedObservedPoints redPoints = new WeightedObservedPoints();
      WeightedObservedPoints greenPoints = new WeightedObservedPoints();
      WeightedObservedPoints bluePoints = new WeightedObservedPoints();

      for (Entry<String, List<Color>> colorColumns :
          chipsPerColorSquare.row(addedColor).entrySet()) {
        String baseColor = colorColumns.getKey();
        if (addedColor.equals(baseColor)) {
          // Skip "key" color squares
          continue;
        }

        // Preload base color as initial for transition points.
        Color lastColor = filamentColors.get(baseColor);
        for (Color color : colorColumns.getValue()) {
          mappings.put(lastColor, color);

          redPoints.add(lastColor.getRed(), color.getRed());
          greenPoints.add(lastColor.getGreen(), color.getGreen());
          bluePoints.add(lastColor.getBlue(), color.getBlue());

          lastColor = color;
        }
      }

      PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
      RGBCoefficients coefficients =
          RGBCoefficients.create(
              fitter.fit(redPoints.toList()),
              fitter.fit(greenPoints.toList()),
              fitter.fit(bluePoints.toList()));
      filamentCoefficients.put(addedColor, coefficients);
      filamentColorMappings.put(addedColor, mappings);

      logger.atInfo().log(
          """
          Color coefficients for %s:
                🔴 = %s%.5f𝒓² %s𝒓 %s
                🟢 = %s%.5f𝒈² %s𝒈 %s
                🔵 = %s%.5f𝒃² %s𝒃 %s""",
          addedFilament.getKey(),
          coefficients.r().a() < 0 ? "" : " ",
          coefficients.r().a(),
          formatCoefficient(coefficients.r().b()),
          formatCoefficient(coefficients.r().c()),
          coefficients.g().a() < 0 ? "" : " ",
          coefficients.g().a(),
          formatCoefficient(coefficients.g().b()),
          formatCoefficient(coefficients.g().c()),
          coefficients.b().a() < 0 ? "" : " ",
          coefficients.b().a(),
          formatCoefficient(coefficients.b().b()),
          formatCoefficient(coefficients.b().c()));
    }
  }

  public void analyze(
      BufferedImage image, List<String> filamentNames, int numChipRows, int numChipCols) {
    numColors = filamentNames.size();
    this.filamentNames = ImmutableList.copyOf(filamentNames);
    this.numChipRows = numChipRows;
    this.numChipCols = numChipCols;
    numChipRowsPerColor = numChipRows / numColors;
    numChipColsPerColor = numChipCols / numColors;

    loadChipColors(image);
    findUniformColorGrids();

    if (numColors > 1) {
      getCoefficients();
    }

    ImmutableMap.Builder<String, FilamentData> newFilamentData = ImmutableMap.builder();
    if (filamentData != null) {
      newFilamentData.putAll(filamentData);
    }

    for (Entry<String, Color> filamentColor : filamentColors.entrySet()) {
      String colorName = filamentColor.getKey();
      Color color = filamentColor.getValue();

      FilamentData.Builder dataBuilder = FilamentData.builder();
      dataBuilder.color(color);
      Map<Color, Color> mappings = new HashMap<>();
      if (filamentColorMappings != null) {
        mappings = filamentColorMappings.get(colorName);
      }
      dataBuilder.mappings(mappings);

      RGBCoefficients coefficients = RGBCoefficients.ZERO;
      if (filamentCoefficients != null) {
        coefficients = filamentCoefficients.get(colorName);
      }
      dataBuilder.coefficients(coefficients);

      newFilamentData.put(colorName, dataBuilder.build());
    }

    filamentData = newFilamentData.build();
  }

  public static ColorAnalyzer analyze(
      String imagePath, List<String> filamentNames, int numChipRows, int numChipCols)
      throws IOException {
    ColorAnalyzer analyzer = new ColorAnalyzer();
    analyzer.analyze(ImageIO.read(new File(imagePath)), filamentNames, numChipRows, numChipCols);

    return analyzer;
  }
}
