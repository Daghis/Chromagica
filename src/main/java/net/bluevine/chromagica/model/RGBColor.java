package net.bluevine.chromagica.model;

import com.google.auto.value.AutoValue;
import java.awt.Color;

@AutoValue
public abstract class RGBColor implements Comparable<RGBColor> {
  public abstract int r();
  public abstract int g();
  public abstract int b();

  public static RGBColor create(Color color) {
    return new AutoValue_RGBColor(color.getRed(), color.getGreen(), color.getBlue());
  }
  public static RGBColor create(int r, int g, int b) {
    return new AutoValue_RGBColor(r, g, b);
  }

  @Override
  public int compareTo(RGBColor other) {
    int result = Integer.compare(this.r(), other.r());
    if (result != 0) return result;

    result = Integer.compare(this.g(), other.g());
    if (result != 0) return result;

    return Integer.compare(this.b(), other.b());
  }
}
