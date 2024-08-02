package net.bluevine.chromagica;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static net.bluevine.chromagica.ColorUtil.calculateDifference;
import static net.bluevine.chromagica.ColorUtil.getAverageColor;

import com.google.common.collect.ImmutableList;
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
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

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

  private static final double CHIP_COLOR_VIGNETTE = 0.8;

  private ColorAnalyzer() {}

  private Color getAverageColorOfChip(BufferedImage image, int x, int y) {
    double chipWidth = (double) image.getWidth() / numChipCols;
    double chipHeight = (double) image.getHeight() / numChipRows;

    int vignetteWidth = (int) (chipWidth * CHIP_COLOR_VIGNETTE);
    int vignetteHeight = (int) (chipHeight * CHIP_COLOR_VIGNETTE);

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
    return String.format("%c %.5f", value < 0 ? '−' : '+', Math.abs(value));
  }

  private void getCoefficients() {
    for (Entry<String, Color> addedFilament : filamentColors.entrySet()) {
      WeightedObservedPoints redPoints = new WeightedObservedPoints();
      WeightedObservedPoints greenPoints = new WeightedObservedPoints();
      WeightedObservedPoints bluePoints = new WeightedObservedPoints();

      for (Entry<String, List<Color>> colorColumns :
          chipsPerColorSquare.row(addedFilament.getKey()).entrySet()) {
        if (addedFilament.getKey().equals(colorColumns.getKey())) {
          // Skip "key" color squares
          continue;
        }

        Color lastColor = filamentColors.get(colorColumns.getKey());
        for (Color color : colorColumns.getValue()) {

          redPoints.add(lastColor.getRed(), color.getRed());
          greenPoints.add(lastColor.getGreen(), color.getGreen());
          bluePoints.add(lastColor.getBlue(), color.getBlue());

          lastColor = color;
        }
      }

      PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
      double[] redCoefficients = fitter.fit(redPoints.toList());
      double[] greenCoefficients = fitter.fit(greenPoints.toList());
      double[] blueCoefficients = fitter.fit(bluePoints.toList());

      logger.atInfo().log(
          """
          Color coefficients for %s:
                🔴 = %.5f𝒓² %s𝒓 %s
                🟢 = %.5f𝒈² %s𝒈 %s
                🔵 = %.5f𝒃² %s𝒃 %s""",
          addedFilament.getKey(),
          redCoefficients[2],
          formatCoefficient(redCoefficients[1]),
          formatCoefficient(redCoefficients[0]),
          greenCoefficients[2],
          formatCoefficient(greenCoefficients[1]),
          formatCoefficient(greenCoefficients[0]),
          blueCoefficients[2],
          formatCoefficient(blueCoefficients[1]),
          formatCoefficient(blueCoefficients[0]));
    }
  }

  public void analyze(
      String imagePath, List<String> filamentNames, int numChipRows, int numChipCols)
      throws IOException {
    numColors = filamentNames.size();
    this.filamentNames = ImmutableList.copyOf(filamentNames);
    this.numChipRows = numChipRows;
    this.numChipCols = numChipCols;
    numChipRowsPerColor = numChipRows / numColors;
    numChipColsPerColor = numChipCols / numColors;

    loadChipColors(ImageIO.read(new File(imagePath)));
    findUniformColorGrids();
    getCoefficients();
  }
}
