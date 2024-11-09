package net.bluevine.chromagica.loadstepwedge;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.Set;
import net.bluevine.chromagica.common.model.RGBColor;

public class TestData {
  public static final Path FILAMENT_DATABASE_PATH =
      Path.of(requireNonNull(TestData.class.getResource("/filament_database.json")).getPath());
  public static final Set<String> FILAMENT_NAMES =
      Set.of(
          "Beige",
          "Black",
          "Blue",
          "Cyan",
          "Evergreen",
          "Magenta",
          "Orange",
          "Purple",
          "Red",
          "White",
          "Yellow");

  public static final String ORANGE_1x1_IMAGE_PATH =
      requireNonNull(TestData.class.getResource("/Orange-1x1.png")).getPath();
  public static final int ORANGE_1x1_CHIP_ROWS = 1;
  public static final int ORANGE_1x1_CHIP_COLS = 1;
  public static final RGBColor ORANGE_1x1_COLOR = new RGBColor(255, 100, 25);

  public static final String YELLOW_4x3_IMAGE_PATH =
      requireNonNull(TestData.class.getResource("/Yellow-4x3.png")).getPath();
  public static final int YELLOW_4x3_CHIP_ROWS = 1;
  public static final int YELLOW_4x3_CHIP_COLS = 1;
  public static final RGBColor YELLOW_4x3_COLOR = new RGBColor(255, 255, 100);

  public static final String GREEN_YELLOW_2x2_IMAGE_PATH =
      requireNonNull(TestData.class.getResource("/Green+Yellow-2x2.png")).getPath();
  public static final int GREEN_YELLOW_2x2_CHIP_ROWS = 2;
  public static final int GREEN_YELLOW_2x2_CHIP_COLS = 2;
  public static final RGBColor GREEN_2x2_COLOR = new RGBColor(0, 255, 0);
  public static final RGBColor YELLOW_2x2_COLOR = new RGBColor(255, 255, 0);

  public static final String PURPLE_GRAY_4x4_IMAGE_PATH =
      requireNonNull(TestData.class.getResource("/Purple+Gray-4x4.png")).getPath();
  public static final String ROTATED_4x4_IMAGE_PATH =
      requireNonNull(TestData.class.getResource("/Purple+Gray-4x4-rotated.png")).getPath();
  public static final int PURPLE_GRAY_4x4_CHIP_ROWS = 4;
  public static final int PURPLE_GRAY_4x4_CHIP_COLS = 4;
  public static final RGBColor PURPLE_4x4_COLOR = new RGBColor(100, 25, 150);
  public static final RGBColor GRAY_4x4_COLOR = new RGBColor(150, 180, 200);
}
