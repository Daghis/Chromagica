package net.bluevine.chromagica.model;

import com.google.auto.value.AutoValue;
import java.util.Map;

@AutoValue
public abstract class FilamentData {
  public abstract RGBColor color();
  public abstract RGBCoefficients coefficients();
  public abstract Map<RGBColor, RGBColor> mappings();

  public static Builder builder() {
    return new AutoValue_FilamentData.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder color(RGBColor color);
    public abstract Builder coefficients(RGBCoefficients coefficients);
    public abstract Builder mappings(Map<RGBColor, RGBColor> mappings);
    public abstract FilamentData build();
  }
}