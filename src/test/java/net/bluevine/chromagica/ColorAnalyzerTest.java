package net.bluevine.chromagica;

import static java.util.Objects.requireNonNull;
import static net.bluevine.chromagica.TestData.GRAY_4x4_COLOR;
import static net.bluevine.chromagica.TestData.GREEN_2x2_COLOR;
import static net.bluevine.chromagica.TestData.GREEN_YELLOW_2x2_CHIP_COLS;
import static net.bluevine.chromagica.TestData.GREEN_YELLOW_2x2_CHIP_ROWS;
import static net.bluevine.chromagica.TestData.GREEN_YELLOW_2x2_IMAGE_PATH;
import static net.bluevine.chromagica.TestData.ORANGE_1x1_CHIP_COLS;
import static net.bluevine.chromagica.TestData.ORANGE_1x1_CHIP_ROWS;
import static net.bluevine.chromagica.TestData.ORANGE_1x1_COLOR;
import static net.bluevine.chromagica.TestData.ORANGE_1x1_IMAGE_PATH;
import static net.bluevine.chromagica.TestData.PURPLE_4x4_COLOR;
import static net.bluevine.chromagica.TestData.PURPLE_GRAY_4x4_CHIP_COLS;
import static net.bluevine.chromagica.TestData.PURPLE_GRAY_4x4_CHIP_ROWS;
import static net.bluevine.chromagica.TestData.PURPLE_GRAY_4x4_IMAGE_PATH;
import static net.bluevine.chromagica.TestData.ROTATED_4x4_IMAGE_PATH;
import static net.bluevine.chromagica.TestData.YELLOW_2x2_COLOR;
import static net.bluevine.chromagica.TestData.YELLOW_4x3_CHIP_COLS;
import static net.bluevine.chromagica.TestData.YELLOW_4x3_CHIP_ROWS;
import static net.bluevine.chromagica.TestData.YELLOW_4x3_COLOR;
import static net.bluevine.chromagica.TestData.YELLOW_4x3_IMAGE_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ColorAnalyzerTest {
  private ColorAnalyzer analyzer;

  @BeforeEach
  public void setUp() {
    analyzer = new ColorAnalyzer(Map.of());
  }

  @Test
  void analyze_simpleImage() throws Exception {
    String colorName = "Orange";

    analyzer.analyze(
        ORANGE_1x1_IMAGE_PATH, List.of(colorName), ORANGE_1x1_CHIP_ROWS, ORANGE_1x1_CHIP_COLS);

    assertEquals(colorName, Iterables.getOnlyElement(analyzer.filamentData.keySet()));
    assertEquals(
        ORANGE_1x1_COLOR, Iterables.getOnlyElement(analyzer.filamentData.values()).getColor());
  }

  @Test
  void analyze_multiplePixelsPerChip_sameColor() throws Exception {
    String colorName = "Yellow";

    analyzer.analyze(
        YELLOW_4x3_IMAGE_PATH, List.of(colorName), YELLOW_4x3_CHIP_ROWS, YELLOW_4x3_CHIP_COLS);

    assertEquals(colorName, Iterables.getOnlyElement(analyzer.filamentData.keySet()));
    assertEquals(
        YELLOW_4x3_COLOR, Iterables.getOnlyElement(analyzer.filamentData.values()).getColor());
  }

  @Test
  void analyze_multiplePixelsPerChip_averagedColor() throws Exception {
    String greenColor = "Green";
    String yellowColor = "Yellow";
    List<String> colorNames = List.of(greenColor, yellowColor);

    analyzer.analyze(
        GREEN_YELLOW_2x2_IMAGE_PATH,
        colorNames,
        GREEN_YELLOW_2x2_CHIP_ROWS,
        GREEN_YELLOW_2x2_CHIP_COLS);

    assertThat(analyzer.filamentData.keySet(), containsInAnyOrder(colorNames.toArray()));
    assertEquals(GREEN_2x2_COLOR, requireNonNull(analyzer.filamentData.get(greenColor)).getColor());
    assertEquals(
        YELLOW_2x2_COLOR, requireNonNull(analyzer.filamentData.get(yellowColor)).getColor());
  }

  @Test
  void analyze_coefficients() throws Exception {
    String purpleColor = "Purple";
    String grayColor = "Gray";
    List<String> colorNames = List.of(purpleColor, grayColor);

    analyzer.analyze(
        PURPLE_GRAY_4x4_IMAGE_PATH,
        colorNames,
        PURPLE_GRAY_4x4_CHIP_ROWS,
        PURPLE_GRAY_4x4_CHIP_COLS);

    assertEquals(
        PURPLE_4x4_COLOR, requireNonNull(analyzer.filamentData.get(purpleColor)).getColor());
    assertEquals(GRAY_4x4_COLOR, requireNonNull(analyzer.filamentData.get(grayColor)).getColor());
  }

  @Test
  void analyze_coefficients_rotated() throws Exception {
    String purpleColor = "Purple";
    String grayColor = "Gray";
    List<String> colorNames = List.of(purpleColor, grayColor);

    analyzer.analyze(
        ROTATED_4x4_IMAGE_PATH, colorNames, PURPLE_GRAY_4x4_CHIP_ROWS, PURPLE_GRAY_4x4_CHIP_COLS);

    assertEquals(
        PURPLE_4x4_COLOR, requireNonNull(analyzer.filamentData.get(purpleColor)).getColor());
    assertEquals(GRAY_4x4_COLOR, requireNonNull(analyzer.filamentData.get(grayColor)).getColor());
  }
}
