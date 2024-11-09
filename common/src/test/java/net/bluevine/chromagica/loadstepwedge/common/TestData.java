package net.bluevine.chromagica.loadstepwedge.common;

import static java.util.Collections.emptyMap;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.bluevine.chromagica.common.model.FilamentData;
import net.bluevine.chromagica.common.model.RGBCoefficients;
import net.bluevine.chromagica.common.model.RGBColor;

public class TestData {

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
