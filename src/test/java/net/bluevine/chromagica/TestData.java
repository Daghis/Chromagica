package net.bluevine.chromagica;

import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableMap;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import net.bluevine.chromagica.model.FilamentData;
import net.bluevine.chromagica.model.RGBCoefficients;
import net.bluevine.chromagica.model.RGBColor;

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

  public static final RGBColor WHITE = new RGBColor(255, 255, 255);
  public static final RGBColor BLACK = new RGBColor(0, 0, 0);
  public static final RGBColor GRAY = new RGBColor(128, 128, 128);
  public static final RGBColor LAVENDER = new RGBColor(230, 230, 250);
  public static final RGBColor TURQUOISE = new RGBColor(64, 224, 208);
  public static final RGBColor PLUM = new RGBColor(221, 160, 221);
  public static final RGBColor INDIGO = new RGBColor(75, 0, 130);

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
    "coefficients": [
      [
        0.01,
        17.5,
        75.0
      ],
      [
        -0.02,
        3.14,
        -25.0
      ],
      [
        -0.05,
        1.1,
        155.32
      ]
    ],
    "color": [
      0,
      0,
      0
    ],
    "mappings": [
      [
        [
          128,
          128,
          128
        ],
        [
          0,
          0,
          0
        ]
      ],
      [
        [
          255,
          255,
          255
        ],
        [
          128,
          128,
          128
        ]
      ]
    ]
  },
  "Lavender": {
    "coefficients": [
      [
        0.0,
        0.0,
        0.0
      ],
      [
        0.0,
        0.0,
        0.0
      ],
      [
        0.0,
        0.0,
        0.0
      ]
    ],
    "color": [
      230,
      230,
      250
    ],
    "mappings": [
      [
        [
          64,
          224,
          208
        ],
        [
          230,
          230,
          250
        ]
      ],
      [
        [
          75,
          0,
          130
        ],
        [
          221,
          160,
          221
        ]
      ],
      [
        [
          221,
          160,
          221
        ],
        [
          64,
          224,
          208
        ]
      ]
    ]
  }
}""";

  public static final String BLUE_NAME = "Blue";
  public static final RGBColor BLUE_COLOR = new RGBColor(7, 36, 153);
  public static final RGBCoefficients BLUE_COEFFICIENTS =
      new RGBCoefficients(
          new double[] {4.481, 0.444, -6.000E-4},
          new double[] {24.751, 0.357, 4.944E-4},
          new double[] {20.89, 0.946, -5.933E-4});
  public static final FilamentData BLUE_FILAMENT_DATA =
      new FilamentData(BLUE_COLOR, BLUE_COEFFICIENTS, emptyMap());

  public static final String CYAN_NAME = "Cyan";
  public static final RGBColor CYAN_COLOR = new RGBColor(18, 94, 195);
  public static final RGBCoefficients CYAN_COEFFICIENTS =
      new RGBCoefficients(
          new double[] {10.105, 0.561, -9.997E-4},
          new double[] {45.473, 0.564, -1.587E-4},
          new double[] {50.81, 0.823, -4.453E-4});
  public static final FilamentData CYAN_FILAMENT_DATA =
      new FilamentData(CYAN_COLOR, CYAN_COEFFICIENTS, emptyMap());

  public static final String WHITE_NAME = "White";
  public static final RGBColor WHITE_COLOR = new RGBColor(254, 254, 254);
  public static final RGBCoefficients WHITE_COEFFICIENTS =
      new RGBCoefficients(
          new double[] {64.881, 0.772, -1.465E-4},
          new double[] {62.178, 0.897, -6.616E-4},
          new double[] {60.342, 0.928, -7.245E-4});
  public static final FilamentData WHITE_FILAMENT_DATA =
      new FilamentData(WHITE_COLOR, WHITE_COEFFICIENTS, emptyMap());

  public static final Map<String, FilamentData> TEST_FILAMENT_DATA_MAP =
      ImmutableMap.of(
          BLUE_NAME,
          BLUE_FILAMENT_DATA,
          CYAN_NAME,
          CYAN_FILAMENT_DATA,
          WHITE_NAME,
          WHITE_FILAMENT_DATA);
}
