package net.bluevine.chromagica;

import static java.util.Objects.requireNonNull;
import static net.bluevine.chromagica.TestData.GREEN_COLOR;
import static net.bluevine.chromagica.TestData.GREEN_YELLOW_2x2_CHIP_COLS;
import static net.bluevine.chromagica.TestData.GREEN_YELLOW_2x2_CHIP_ROWS;
import static net.bluevine.chromagica.TestData.GREEN_YELLOW_2x2_IMAGE_PATH;
import static net.bluevine.chromagica.TestData.ORANGE_1x1_CHIP_COLS;
import static net.bluevine.chromagica.TestData.ORANGE_1x1_CHIP_ROWS;
import static net.bluevine.chromagica.TestData.ORANGE_1x1_COLOR;
import static net.bluevine.chromagica.TestData.ORANGE_1x1_IMAGE_PATH;
import static net.bluevine.chromagica.TestData.YELLOW_4x3_CHIP_COLS;
import static net.bluevine.chromagica.TestData.YELLOW_4x3_CHIP_ROWS;
import static net.bluevine.chromagica.TestData.YELLOW_4x3_COLOR;
import static net.bluevine.chromagica.TestData.YELLOW_4x3_IMAGE_PATH;
import static net.bluevine.chromagica.TestData.YELLOW_COLOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.Iterables;
import java.util.List;
import org.junit.jupiter.api.Test;

class ColorAnalyzerTest {
  @Test
  void analyze_simpleImage() throws Exception {
    String colorName = "Orange";

    ColorAnalyzer analyzer =
        ColorAnalyzer.analyze(
            ORANGE_1x1_IMAGE_PATH, List.of(colorName), ORANGE_1x1_CHIP_ROWS, ORANGE_1x1_CHIP_COLS);

    assertEquals(colorName, Iterables.getOnlyElement(analyzer.filamentData.keySet()));
    assertEquals(
        ORANGE_1x1_COLOR, Iterables.getOnlyElement(analyzer.filamentData.values()).color());
  }

  @Test
  void analyze_multiplePixelsPerChip_sameColor() throws Exception {
    String colorName = "Yellow";

    ColorAnalyzer analyzer =
        ColorAnalyzer.analyze(
            YELLOW_4x3_IMAGE_PATH, List.of(colorName), YELLOW_4x3_CHIP_ROWS, YELLOW_4x3_CHIP_COLS);

    assertEquals(colorName, Iterables.getOnlyElement(analyzer.filamentData.keySet()));
    assertEquals(
        YELLOW_4x3_COLOR, Iterables.getOnlyElement(analyzer.filamentData.values()).color());
  }

  @Test
  void analyze_multiplePixelsPerChip_averagedColor() throws Exception {
    String greenColor = "Green";
    String yellowColor = "Yellow";
    List<String> colorNames = List.of(greenColor, yellowColor);

    ColorAnalyzer analyzer =
        ColorAnalyzer.analyze(
            GREEN_YELLOW_2x2_IMAGE_PATH,
            colorNames,
            GREEN_YELLOW_2x2_CHIP_ROWS,
            GREEN_YELLOW_2x2_CHIP_COLS);

    assertThat(analyzer.filamentData.keySet(), containsInAnyOrder(colorNames.toArray()));
    assertEquals(GREEN_COLOR, requireNonNull(analyzer.filamentData.get(greenColor)).color());
    assertEquals(YELLOW_COLOR, requireNonNull(analyzer.filamentData.get(yellowColor)).color());
  }
}
