package net.bluevine.chromagica;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import net.bluevine.chromagica.model.FilamentData;
import net.bluevine.chromagica.model.RGBCoefficients;
import net.bluevine.chromagica.model.RGBColor;

public class TestData {
  public static final String ORANGE_1x1_IMAGE_PATH =
      requireNonNull(TestData.class.getResource("/Orange-1x1.png")).getFile();
  public static final int ORANGE_1x1_CHIP_ROWS = 1;
  public static final int ORANGE_1x1_CHIP_COLS = 1;
  public static final RGBColor ORANGE_1x1_COLOR = new RGBColor(255, 100, 25);

  public static final String YELLOW_4x3_IMAGE_PATH =
      requireNonNull(TestData.class.getResource("/Yellow-4x3.png")).getFile();
  public static final int YELLOW_4x3_CHIP_ROWS = 1;
  public static final int YELLOW_4x3_CHIP_COLS = 1;
  public static final RGBColor YELLOW_4x3_COLOR = new RGBColor(255, 255, 100);

  public static final String GREEN_YELLOW_2x2_IMAGE_PATH =
      requireNonNull(TestData.class.getResource("/Green+Yellow-2x2.png")).getFile();
  public static final int GREEN_YELLOW_2x2_CHIP_ROWS = 2;
  public static final int GREEN_YELLOW_2x2_CHIP_COLS = 2;
  public static final RGBColor GREEN_2x2_COLOR = new RGBColor(0, 255, 0);
  public static final RGBColor YELLOW_2x2_COLOR = new RGBColor(255, 255, 0);

  public static final String PURPLE_GRAY_4x4_IMAGE_PATH =
      requireNonNull(TestData.class.getResource("/Purple+Gray-4x4.png")).getFile();
  public static final String ROTATED_4x4_IMAGE_PATH =
      requireNonNull(TestData.class.getResource("/Purple+Gray-4x4-rotated.png")).getFile();
  public static final int PURPLE_GRAY_4x4_CHIP_ROWS = 4;
  public static final int PURPLE_GRAY_4x4_CHIP_COLS = 4;
  public static final RGBColor PURPLE_4x4_COLOR = new RGBColor(100, 25, 150);
  public static final RGBColor GRAY_4x4_COLOR = new RGBColor(150, 180, 200);

  public static final RGBColor WHITE = new RGBColor(255, 255, 255);
  public static final RGBColor BLACK = new RGBColor(0, 0, 0);
  public static final RGBColor GRAY = new RGBColor(128, 128, 128);
  public static final RGBColor LAVENDER = new RGBColor(230, 230, 250);
  public static final RGBColor SALMON = new RGBColor(250, 128, 114);
  public static final RGBColor TURQUOISE = new RGBColor(64, 224, 208);
  public static final RGBColor GOLDENROD = new RGBColor(218, 165, 32);
  public static final RGBColor PLUM = new RGBColor(221, 160, 221);
  public static final RGBColor INDIGO = new RGBColor(75, 0, 130);
  public static final RGBColor MINT = new RGBColor(189, 252, 201);
  public static final RGBColor CORAL = new RGBColor(255, 127, 80);
  public static final RGBColor OLIVE = new RGBColor(128, 128, 0);
  public static final RGBColor TEAL = new RGBColor(0, 128, 128);

  public static final Map<String, FilamentData> TEST_FILAMENT_DATA =
      Map.of(
          "Black",
          new FilamentData(
              BLACK,
              new RGBCoefficients(
                  new double[] {0.01, 17.5, 75},
                  new double[] {-0.02, 3.14, -25},
                  new double[] {-0.05, 1.1, 155.32}),
              Map.of(WHITE, GRAY, GRAY, BLACK)),
          "Lavender",
          new FilamentData(
              LAVENDER,
              RGBCoefficients.ZERO,
              Map.of(INDIGO, PLUM, PLUM, TURQUOISE, TURQUOISE, LAVENDER)));
  public static final String TEST_FILAMENT_DATA_AS_JSON =
      """
{
  "Black": {
    "coefficients": {
      "b": {
        "a": 155.32,
        "b": 1.1,
        "c": -0.05
      },
      "g": {
        "a": -25.0,
        "b": 3.14,
        "c": -0.02
      },
      "r": {
        "a": 75.0,
        "b": 17.5,
        "c": 0.01
      }
    },
    "color": {
      "b": 0,
      "g": 0,
      "r": 0
    },
    "mappings": [
      {
        "key": {
          "b": 255,
          "g": 255,
          "r": 255
        },
        "value": {
          "b": 128,
          "g": 128,
          "r": 128
        }
      },
      {
        "key": {
          "b": 128,
          "g": 128,
          "r": 128
        },
        "value": {
          "b": 0,
          "g": 0,
          "r": 0
        }
      }
    ]
  },
  "Lavender": {
    "coefficients": {
      "b": {
        "a": 0.0,
        "b": 0.0,
        "c": 0.0
      },
      "g": {
        "a": 0.0,
        "b": 0.0,
        "c": 0.0
      },
      "r": {
        "a": 0.0,
        "b": 0.0,
        "c": 0.0
      }
    },
    "color": {
      "b": 250,
      "g": 230,
      "r": 230
    },
    "mappings": [
      {
        "key": {
          "b": 208,
          "g": 224,
          "r": 64
        },
        "value": {
          "b": 250,
          "g": 230,
          "r": 230
        }
      },
      {
        "key": {
          "b": 221,
          "g": 160,
          "r": 221
        },
        "value": {
          "b": 208,
          "g": 224,
          "r": 64
        }
      },
      {
        "key": {
          "b": 130,
          "g": 0,
          "r": 75
        },
        "value": {
          "b": 221,
          "g": 160,
          "r": 221
        }
      }
    ]
  }
}""";
}
