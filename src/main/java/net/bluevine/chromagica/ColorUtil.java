package net.bluevine.chromagica;

import java.awt.Color;
import java.util.Collection;

public class ColorUtil {
  private ColorUtil() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static double calculateDifference(Color color1, Color color2) {
    double[] lab1 = rgbToLab(color1);
    double[] lab2 = rgbToLab(color2);

    return euclideanDistance(lab1, lab2);
  }

  public static Color getAverageColor(Collection<Color> colors) {
    if (colors == null || colors.isEmpty()) {
      return Color.BLACK;
    }

    int sumRed = 0;
    int sumGreen = 0;
    int sumBlue = 0;
    int count = colors.size();

    for (Color color : colors) {
      sumRed += color.getRed();
      sumGreen += color.getGreen();
      sumBlue += color.getBlue();
    }

    int averageRed = Math.round((float) sumRed / count);
    int averageGreen = Math.round((float) sumGreen / count);
    int averageBlue = Math.round((float) sumBlue / count);

    return new Color(averageRed, averageGreen, averageBlue);
  }

  private static double[] rgbToLab(Color color) {
    float[] rgb = color.getRGBColorComponents(null);
    double[] xyz = rgbToXyz(rgb[0], rgb[1], rgb[2]);
    return xyzToLab(xyz[0], xyz[1], xyz[2]);
  }

  private static double[] rgbToXyz(double r, double g, double b) {
    // Convert RGB to XYZ
    r = pivotRgb(r);
    g = pivotRgb(g);
    b = pivotRgb(b);

    double x = r * 0.4124564 + g * 0.3575761 + b * 0.1804375;
    double y = r * 0.2126729 + g * 0.7151522 + b * 0.0721750;
    double z = r * 0.0193339 + g * 0.1191920 + b * 0.9503041;

    return new double[] {x, y, z};
  }

  private static double pivotRgb(double n) {
    return (n > 0.04045) ? Math.pow((n + 0.055) / 1.055, 2.4) : n / 12.92;
  }

  private static double[] xyzToLab(double x, double y, double z) {
    // Convert XYZ to LAB
    x /= 95.047;
    y /= 100.000;
    z /= 108.883;

    x = pivotXyz(x);
    y = pivotXyz(y);
    z = pivotXyz(z);

    double l = (116 * y) - 16;
    double a = 500 * (x - y);
    double b = 200 * (y - z);

    return new double[] {l, a, b};
  }

  private static double pivotXyz(double n) {
    return (n > 0.008856) ? Math.pow(n, 1.0 / 3.0) : (7.787 * n) + (16.0 / 116.0);
  }

  private static double euclideanDistance(double[] lab1, double[] lab2) {
    double deltaL = lab1[0] - lab2[0];
    double deltaA = lab1[1] - lab2[1];
    double deltaB = lab1[2] - lab2[2];

    return Math.sqrt(deltaL * deltaL + deltaA * deltaA + deltaB * deltaB);
  }
}
