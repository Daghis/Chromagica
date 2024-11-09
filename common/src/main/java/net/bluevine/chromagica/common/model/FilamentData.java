package net.bluevine.chromagica.common.model;

import java.util.Map;
import lombok.Value;

@Value
public class FilamentData {
  RGBColor color;
  RGBCoefficients coefficients;
  Map<RGBColor, RGBColor> mappings;
}
