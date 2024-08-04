package net.bluevine.chromagica.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RGBCoefficients {
  private double[][] rgbCoefficients = new double[3][3];

  private static final double[] ZERO_COEFFS = new double[] {0, 0, 0};
  public static final RGBCoefficients ZERO =
      new RGBCoefficients(ZERO_COEFFS, ZERO_COEFFS, ZERO_COEFFS);

  public RGBCoefficients(double[] r, double[] g, double[] b) {
    rgbCoefficients[0] = r;
    rgbCoefficients[1] = g;
    rgbCoefficients[2] = b;
  }

  public double[] getR() {
    return rgbCoefficients[0];
  }

  public double[] getG() {
    return rgbCoefficients[1];
  }

  public double[] getB() {
    return rgbCoefficients[2];
  }
}
