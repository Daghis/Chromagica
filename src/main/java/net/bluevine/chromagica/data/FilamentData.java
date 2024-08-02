package net.bluevine.chromagica.data;

import com.google.auto.value.AutoValue;
import java.awt.Color;
import java.util.Map;

@AutoValue
public abstract class FilamentData {
  public abstract String name();
  public abstract Map<String, RGBCoefficients> coefficients();
  public abstract Map<String, Map<Color, Color>> mappings();
  public abstract Map<String, Color> colors();

  public static Builder builder() {
    return new AutoValue_FilamentData.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder name(String name);
    public abstract Builder coefficients(Map<String, RGBCoefficients> coefficients);
    public abstract Builder mappings(Map<String, Map<Color, Color>> mappings);
    public abstract Builder colors(Map<String, Color> colors);
    public abstract FilamentData build();
  }
}