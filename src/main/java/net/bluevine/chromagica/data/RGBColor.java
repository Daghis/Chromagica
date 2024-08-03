package net.bluevine.chromagica.data;

import com.google.auto.value.AutoValue;
import java.awt.Color;

@AutoValue
public abstract class RGBColor {
  public abstract int r();
  public abstract int g();
  public abstract int b();

  public static RGBColor create(Color color) {
    return new AutoValue_RGBColor(color.getRed(), color.getGreen(), color.getBlue());
  }
  public static RGBColor create(int r, int g, int b) {
    return new AutoValue_RGBColor(r, g, b);
  }
}
