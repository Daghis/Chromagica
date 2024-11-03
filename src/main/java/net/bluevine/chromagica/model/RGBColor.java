package net.bluevine.chromagica.model;

import static org.apache.commons.imaging.color.ColorConversions.convertRgbToXyz;
import static org.apache.commons.imaging.color.ColorConversions.convertXyzToCieLab;

import java.awt.Color;
import lombok.Value;
import org.apache.commons.imaging.color.ColorCieLab;

@Value
public class RGBColor implements Comparable<RGBColor> {
  double[] rgb = new double[3];
  ColorCieLab lab;

  public RGBColor(double r, double g, double b) {
    rgb[0] = r;
    rgb[1] = g;
    rgb[2] = b;

    lab = convertXyzToCieLab(convertRgbToXyz(getRGB()));
  }

  public RGBColor(Color color) {
    this(color.getRed(), color.getGreen(), color.getBlue());
  }

  public RGBColor(int color) {
    this((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
  }

  private double clamp(double value) {
    return Math.max(0, Math.min(255, value));
  }

  public double getR() {
    return rgb[0];
  }

  public int getRInt() {
    return Math.round((float) clamp(rgb[0]));
  }

  public double getG() {
    return rgb[1];
  }

  // Corrected the index from rgb[2] to rgb[1]
  public int getGInt() {
    return Math.round((float) clamp(rgb[1]));
  }

  public double getB() {
    return rgb[2];
  }

  public int getBInt() {
    return Math.round((float) clamp(rgb[2]));
  }

  public int getRGB() {
    // Combine the components into a single integer (0xRRGGBB)
    return 0xff << 24 | getRInt() << 16 | getGInt() << 8 | getBInt();
  }

  public int[] getIntArray() {
    return new int[] {getRInt(), getGInt(), getBInt()};
  }

  /**
   * Computes the Euclidean distance between two RGBColor instances in Lab color space.
   *
   * @param other the other RGBColor
   * @return the Euclidean distance between this object and other in Lab space
   */
  public double computeLabDistance(RGBColor other) {
    double deltaL = lab.l - other.lab.l;
    double deltaA = lab.a - other.lab.a;
    double deltaB = lab.b - other.lab.b;

    return Math.sqrt(deltaL * deltaL + deltaA * deltaA + deltaB * deltaB);
  }

  @Override
  public int compareTo(RGBColor other) {
    int result = Double.compare(this.getR(), other.getR());
    if (result != 0) return result;

    result = Double.compare(this.getG(), other.getG());
    if (result != 0) return result;

    return Double.compare(this.getB(), other.getB());
  }

  @Override
  public String toString() {
    return String.format(
        "RGBColor(rgb=#%06x, lab={L=%.2f, a=%.2f, b=%.2f})", getRGB(), lab.l, lab.a, lab.b);
  }
}
