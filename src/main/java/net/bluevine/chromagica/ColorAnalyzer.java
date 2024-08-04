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
import net.bluevine.chromagica.model.FilamentData;
import net.bluevine.chromagica.model.RGBCoefficients;
import net.bluevine.chromagica.model.RGBColor;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class ColorAnalyzer {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  public Map<String, FilamentData> filamentData;

  private int numColors;
  private List<String> filamentNames;

  private int numChipRows;
  private int numChipCols;

  private int numChipRowsPerColor;
  private int numChipColsPerColor;

  private Table<String, String, List<RGBColor>> chipsPerColorSquare;
  private Map<String, RGBColor> filamentColors;
  private Map<String, Map<RGBColor, RGBColor>> filamentColorMappings;
  private Map<String, RGBCoefficients> filamentCoefficients;

  private static final double CHIP_COLOR_VIGNETTE = 0.8;

  private static final char PLUS_SIGN = '+';
  private static final char MINUS_SIGN = '−';

  public ColorAnalyzer(Map<String, FilamentData> filamentData) {
    checkNotNull(filamentData);
    this.filamentData = ImmutableMap.copyOf(filamentData);
  }

  private RGBColor getAverageColorOfChip(BufferedImage image, int x, int y) {
    double chipWidth = (double) image.getWidth() / numChipCols;
    double chipHeight = (double) image.getHeight() / numChipRows;

    int vignetteWidth = (int) Math.max(1, (chipWidth * CHIP_COLOR_VIGNETTE));
    int vignetteHeight = (int) Math.max(1, (chipHeight * CHIP_COLOR_VIGNETTE));

    double margin = (1.0 - CHIP_COLOR_VIGNETTE) / 2;
    int left = (int) ((x + margin) * chipWidth);
    int top = (int) ((y + margin) * chipHeight);

    // Get a list of RGB pixel values for the specified region
    List<RGBColor> vignetteColors =
        Arrays.stream(
                image.getRGB(left, top, vignetteWidth, vignetteHeight, null, 0, vignetteWidth))
            .mapToObj(Color::new)
            .map(RGBColor::new)
            .collect(toImmutableList());

    return getAverageColor(vignetteColors);
  }

  private void loadChipColors(BufferedImage image) {
    ImmutableTable.Builder<String, String, List<RGBColor>> perSquareBuilder =
        ImmutableTable.builder();
    for (int colorY = 0; colorY < numColors; colorY++) {
      for (int colorX = 0; colorX < numColors; colorX++) {
        ImmutableList.Builder<RGBColor> colorsBuilder = ImmutableList.builder();
        for (int chipY = colorY * numChipRowsPerColor;
            chipY < (colorY + 1) * numChipRowsPerColor;
            chipY++) {
          for (int chipX = colorX * numChipColsPerColor;
              chipX < (colorX + 1) * numChipColsPerColor;
              chipX++) {
            RGBColor color = getAverageColorOfChip(image, chipX, chipY);
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

    Map<Diagonal, Map<String, RGBColor>> colorDiagonals = new HashMap<>();
    Map<Diagonal, Double> differences = new HashMap<>();

    for (Diagonal direction : ImmutableList.of(Diagonal.LEFT, Diagonal.RIGHT)) {
      Map<String, Pair<RGBColor, Double>> diagonal = new HashMap<>();

      for (int i = 0; i < numColors; i++) {
        int columnForRow = direction == Diagonal.LEFT ? i : numColors - i - 1;

        List<RGBColor> diagonalChips =
            chipsPerColorSquare.get(filamentNames.get(i), filamentNames.get(columnForRow));
        checkNotNull(diagonalChips);

        RGBColor overallAverageColor = getAverageColor(diagonalChips);
        double maxDistanceFromAverage =
            diagonalChips.stream()
                .map(color -> calculateDifference(color, overallAverageColor))
                .max(Double::compare)
                .orElse(0.0);
        diagonal.put(filamentNames.get(i), Pair.of(overallAverageColor, maxDistanceFromAverage));
      }

      // Need to ensure that colors are in the proper order. It's still important here.
      Map<String, RGBColor> colorsAlongDiagonal =
          diagonal.entrySet().stream()
              .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().getKey()));
      colorDiagonals.put(direction, colorsAlongDiagonal);

      double distances = diagonal.values().stream().mapToDouble(Pair::getValue).sum();
      differences.put(direction, distances);
    }

    if (differences.get(Diagonal.LEFT) <= differences.get(Diagonal.RIGHT)) {
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

    for (Entry<String, RGBColor> addedFilament : filamentColors.entrySet()) {
      String addedColor = addedFilament.getKey();

      Map<RGBColor, RGBColor> mappings = new HashMap<>();
      Map<RGBColor, RGBColor> existingMappings = filamentColorMappings.get(addedColor);
      if (existingMappings != null) {
        mappings.putAll(existingMappings);
      }

      WeightedObservedPoints redPoints = new WeightedObservedPoints();
      WeightedObservedPoints greenPoints = new WeightedObservedPoints();
      WeightedObservedPoints bluePoints = new WeightedObservedPoints();

      for (Entry<String, List<RGBColor>> colorColumns :
          chipsPerColorSquare.row(addedColor).entrySet()) {
        String baseColor = colorColumns.getKey();
        if (addedColor.equals(baseColor)) {
          // Skip "key" color squares
          continue;
        }
        // Preload base color as initial for transition points.
        RGBColor lastColor = filamentColors.get(baseColor);
        for (RGBColor color : colorColumns.getValue()) {
          mappings.put(lastColor, color);

          redPoints.add(lastColor.getR(), color.getR());
          greenPoints.add(lastColor.getG(), color.getG());
          bluePoints.add(lastColor.getB(), color.getB());

          lastColor = color;
        }
      }

      PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
      RGBCoefficients coefficients =
          new RGBCoefficients(
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
          coefficients.getR()[2] < 0 ? "" : " ",
          coefficients.getR()[2],
          formatCoefficient(coefficients.getR()[1]),
          formatCoefficient(coefficients.getR()[0]),
          coefficients.getG()[2] < 0 ? "" : " ",
          coefficients.getG()[2],
          formatCoefficient(coefficients.getG()[1]),
          formatCoefficient(coefficients.getG()[0]),
          coefficients.getB()[2] < 0 ? "" : " ",
          coefficients.getB()[2],
          formatCoefficient(coefficients.getB()[1]),
          formatCoefficient(coefficients.getB()[0]));
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

    getCoefficients();

    Map<String, FilamentData> newFilamentData = new HashMap<>();
    if (filamentData != null) {
      newFilamentData.putAll(filamentData);
    }

    for (Entry<String, RGBColor> filamentColor : filamentColors.entrySet()) {
      String colorName = filamentColor.getKey();
      RGBColor color = filamentColor.getValue();

      FilamentData data = new FilamentData();
      data.setColor(color);
      Map<RGBColor, RGBColor> mappings = new HashMap<>();
      if (filamentColorMappings != null) {
        mappings = filamentColorMappings.get(colorName);
      }
      data.setMappings(mappings);

      RGBCoefficients coefficients = RGBCoefficients.ZERO;
      if (filamentCoefficients != null) {
        coefficients = filamentCoefficients.get(colorName);
      }
      data.setCoefficients(coefficients);

      newFilamentData.put(colorName, data);
    }

    filamentData = newFilamentData;
  }

  public void analyze(
      String imagePath, List<String> filamentNames, int numChipRows, int numChipCols)
      throws IOException {
    analyze(ImageIO.read(new File(imagePath)), filamentNames, numChipRows, numChipCols);
  }
}
