package net.bluevine.chromagica;

import static java.util.Objects.requireNonNull;

import net.bluevine.chromagica.data.RGBColor;

public class TestData {
  public static final String ORANGE_1x1_IMAGE_PATH =
      requireNonNull(TestData.class.getResource("/Orange-1x1.png")).getFile();
  public static final int ORANGE_1x1_CHIP_ROWS = 1;
  public static final int ORANGE_1x1_CHIP_COLS = 1;
  public static final RGBColor ORANGE_1x1_COLOR = RGBColor.create(255, 100, 25);

  public static final String YELLOW_4x3_IMAGE_PATH =
      requireNonNull(TestData.class.getResource("/Yellow-4x3.png")).getFile();
  public static final int YELLOW_4x3_CHIP_ROWS = 1;
  public static final int YELLOW_4x3_CHIP_COLS = 1;
  public static final RGBColor YELLOW_4x3_COLOR = RGBColor.create(255, 255, 100);

  public static final String GREEN_YELLOW_2x2_IMAGE_PATH =
      requireNonNull(TestData.class.getResource("/Green+Yellow-2x2.png")).getFile();
  public static final int GREEN_YELLOW_2x2_CHIP_ROWS = 2;
  public static final int GREEN_YELLOW_2x2_CHIP_COLS = 2;
  public static final RGBColor GREEN_COLOR = RGBColor.create(0, 255, 0);
  public static final RGBColor YELLOW_COLOR = RGBColor.create(255, 255, 0);

  public static final String PURPLE_GRAY_4x4_IMAGE_PATH=
      requireNonNull(TestData.class.getResource("/Purple+Gray-4x4.png")).getFile();
  public static final String ROTATED_4x4_IMAGE_PATH=
      requireNonNull(TestData.class.getResource("/Purple+Gray-4x4-rotated.png")).getFile();
  public static final int PURPLE_GRAY_4x4_CHIP_ROWS = 4;
  public static final int PURPLE_GRAY_4x4_CHIP_COLS = 4;
  public static final RGBColor PURPLE_COLOR = RGBColor.create(100, 25, 150);
  public static final RGBColor GRAY_COLOR = RGBColor.create(150, 180, 200);
}
