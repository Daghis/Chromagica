package net.bluevine.chromagica.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilamentData {
  private RGBColor color;
  private RGBCoefficients coefficients;
  private Map<RGBColor, RGBColor> mappings;
}
