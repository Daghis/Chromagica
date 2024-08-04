package net.bluevine.chromagica;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import net.bluevine.chromagica.model.FilamentData;
import net.bluevine.chromagica.model.RGBCoefficients;
import net.bluevine.chromagica.model.RGBCoefficients.QuadraticCoefficients;
import net.bluevine.chromagica.model.RGBColor;

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
  public static final RGBColor GREEN_2x2_COLOR = RGBColor.create(0, 255, 0);
  public static final RGBColor YELLOW_2x2_COLOR = RGBColor.create(255, 255, 0);

  public static final String PURPLE_GRAY_4x4_IMAGE_PATH =
      requireNonNull(TestData.class.getResource("/Purple+Gray-4x4.png")).getFile();
  public static final String ROTATED_4x4_IMAGE_PATH =
      requireNonNull(TestData.class.getResource("/Purple+Gray-4x4-rotated.png")).getFile();
  public static final int PURPLE_GRAY_4x4_CHIP_ROWS = 4;
  public static final int PURPLE_GRAY_4x4_CHIP_COLS = 4;
  public static final RGBColor PURPLE_4x4_COLOR = RGBColor.create(100, 25, 150);
  public static final RGBColor GRAY_4x4_COLOR = RGBColor.create(150, 180, 200);

  public static final RGBColor WHITE = RGBColor.create(255, 255, 255);
  public static final RGBColor BLACK = RGBColor.create(0, 0, 0);
  public static final RGBColor GRAY = RGBColor.create(128, 128, 128);
  public static final RGBColor LAVENDER = RGBColor.create(230, 230, 250);
  public static final RGBColor SALMON = RGBColor.create(250, 128, 114);
  public static final RGBColor TURQUOISE = RGBColor.create(64, 224, 208);
  public static final RGBColor GOLDENROD = RGBColor.create(218, 165, 32);
  public static final RGBColor PLUM = RGBColor.create(221, 160, 221);
  public static final RGBColor INDIGO = RGBColor.create(75, 0, 130);
  public static final RGBColor MINT = RGBColor.create(189, 252, 201);
  public static final RGBColor CORAL = RGBColor.create(255, 127, 80);
  public static final RGBColor OLIVE = RGBColor.create(128, 128, 0);
  public static final RGBColor TEAL = RGBColor.create(0, 128, 128);

  public static final Map<String, FilamentData> TEST_FILAMENT_DATA =
      Map.of(
          "Black",
          FilamentData.builder()
              .color(BLACK)
              .coefficients(
                  RGBCoefficients.create(
                      QuadraticCoefficients.create(new double[] {0.01, 17.5, 75}),
                      QuadraticCoefficients.create(new double[] {-0.02, 3.14, -25}),
                      QuadraticCoefficients.create(new double[] {-0.05, 1.1, 155.32})))
              .mappings(Map.of(WHITE, GRAY, GRAY, BLACK))
              .build(),
          "Lavender",
          FilamentData.builder()
              .color(LAVENDER)
              .coefficients(RGBCoefficients.ZERO)
              .mappings(Map.of(INDIGO, PLUM, PLUM, TURQUOISE, TURQUOISE, LAVENDER))
              .build());
  public static final String TEST_FILAMENT_DATA_AS_JSON =
      """
{
  "Black": {
    "color": {
      "r": 0,
      "g": 0,
      "b": 0
    },
    "coefficients": {
      "r": {
        "a": 75.0,
        "b": 17.5,
        "c": 0.01
      },
      "g": {
        "a": -25.0,
        "b": 3.14,
        "c": -0.02
      },
      "b": {
        "a": 155.32,
        "b": 1.1,
        "c": -0.05
      }
    },
    "mappings": {
      "RGBColor{r\\u003d128, g\\u003d128, b\\u003d128}": {
        "r": 0,
        "g": 0,
        "b": 0
      },
      "RGBColor{r\\u003d255, g\\u003d255, b\\u003d255}": {
        "r": 128,
        "g": 128,
        "b": 128
      }
    }
  },
  "Lavender": {
    "color": {
      "r": 230,
      "g": 230,
      "b": 250
    },
    "coefficients": {
      "r": {
        "a": 0.0,
        "b": 0.0,
        "c": 0.0
      },
      "g": {
        "a": 0.0,
        "b": 0.0,
        "c": 0.0
      },
      "b": {
        "a": 0.0,
        "b": 0.0,
        "c": 0.0
      }
    },
    "mappings": {
      "RGBColor{r\\u003d64, g\\u003d224, b\\u003d208}": {
        "r": 230,
        "g": 230,
        "b": 250
      },
      "RGBColor{r\\u003d75, g\\u003d0, b\\u003d130}": {
        "r": 221,
        "g": 160,
        "b": 221
      },
      "RGBColor{r\\u003d221, g\\u003d160, b\\u003d221}": {
        "r": 64,
        "g": 224,
        "b": 208
      }
    }
  }
}""";
}
