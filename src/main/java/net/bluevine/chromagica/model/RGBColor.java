package net.bluevine.chromagica.model;

import java.awt.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RGBColor implements Comparable<RGBColor> {
  private int[] rgb = new int[3];

  public RGBColor(Color color) {
    rgb[0] = color.getRed();
    rgb[1] = color.getGreen();
    rgb[2] = color.getBlue();
  }

  public RGBColor(int color) {
    rgb[0] = (color >> 16) & 0xFF;
    rgb[1] = (color >> 8) & 0xFF;
    rgb[2] = (color) & 0xFF;
  }

  public RGBColor(int r, int g, int b) {
    rgb[0] = r;
    rgb[1] = g;
    rgb[2] = b;
  }

  public int getR() {
    return rgb[0];
  }

  public int getG() {
    return rgb[1];
  }

  public int getB() {
    return rgb[2];
  }

  @Override
  public int compareTo(RGBColor other) {
    int result = Integer.compare(this.getR(), other.getR());
    if (result != 0) return result;

    result = Integer.compare(this.getG(), other.getG());
    if (result != 0) return result;

    return Integer.compare(this.getB(), other.getB());
  }
}
