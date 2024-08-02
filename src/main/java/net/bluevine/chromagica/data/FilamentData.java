package net.bluevine.chromagica.data;

import com.google.auto.value.AutoValue;
import java.awt.Color;
import java.util.Map;

@AutoValue
public abstract class FilamentData {
  public abstract Color color();
  public abstract RGBCoefficients coefficients();
  public abstract Map<Color, Color> mappings();

  public static Builder builder() {
    return new AutoValue_FilamentData.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder color(Color color);
    public abstract Builder coefficients(RGBCoefficients coefficients);
    public abstract Builder mappings(Map<Color, Color> mappings);
    public abstract FilamentData build();
  }
}