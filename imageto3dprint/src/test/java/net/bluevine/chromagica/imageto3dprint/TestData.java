package net.bluevine.chromagica.imageto3dprint;

import static java.util.Collections.emptyMap;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.bluevine.chromagica.common.model.FilamentData;
import net.bluevine.chromagica.common.model.RGBCoefficients;
import net.bluevine.chromagica.common.model.RGBColor;

public class TestData {
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
