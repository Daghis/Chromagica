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

  public static final RGBColor BLUE_COLOR = new RGBColor(7, 36, 153);
  public static final RGBCoefficients BLUE_COEFFICIENTS =
      new RGBCoefficients(
          new double[] {4.480981432791474, 0.4443952684960717, -6.000013383278259E-4},
          new double[] {24.75142700938747, 0.35721514086022566, 4.944319721599738E-4},
          new double[] {20.889749735873615, 0.9464429696439061, -5.933063972022456E-4});
  public static final FilamentData BLUE_FILAMENT_DATA =
      new FilamentData(BLUE_COLOR, BLUE_COEFFICIENTS, emptyMap());

  public static final RGBColor CYAN_COLOR = new RGBColor(18, 94, 195);
  public static final RGBCoefficients CYAN_COEFFICIENTS =
      new RGBCoefficients(
          new double[] {10.104933275172417, 0.5606841631632997, -9.996605964241558E-4},
          new double[] {45.47346601829285, 0.5641957131366364, -1.5870963339774738E-4},
          new double[] {50.81003367840157, 0.8225173880459974, -4.452842482286347E-4});
  public static final FilamentData CYAN_FILAMENT_DATA =
      new FilamentData(CYAN_COLOR, CYAN_COEFFICIENTS, emptyMap());

  public static final RGBColor WHITE_COLOR = new RGBColor(254, 254, 254);
  public static final RGBCoefficients WHITE_COEFFICIENTS =
      new RGBCoefficients(
          new double[] {64.88128463300333, 0.7721479787432053, -1.4653425210567843E-4},
          new double[] {62.17763968106627, 0.8971743277462471, -6.616349721127205E-4},
          new double[] {60.3418795814688, 0.9280928848575695, -7.244634245743158E-4});
  public static final FilamentData WHITE_FILAMENT_DATA =
      new FilamentData(WHITE_COLOR, WHITE_COEFFICIENTS, emptyMap());

  public static final Map<String, FilamentData> TEST_FILAMENT_DATA_MAP =
      ImmutableMap.of(
          "Blue", BLUE_FILAMENT_DATA, "Cyan", CYAN_FILAMENT_DATA, "White", WHITE_FILAMENT_DATA);
}
