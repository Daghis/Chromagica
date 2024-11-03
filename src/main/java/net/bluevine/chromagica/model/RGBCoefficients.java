package net.bluevine.chromagica.model;

import lombok.Value;

@Value
public class RGBCoefficients {
  double[][] rgbCoefficients = new double[3][3];

  private static final double[] ZERO_COEFFS = new double[] {0, 0, 0};
  public static final RGBCoefficients ZERO =
      new RGBCoefficients(ZERO_COEFFS, ZERO_COEFFS, ZERO_COEFFS);

  public RGBCoefficients(double[] r, double[] g, double[] b) {
    rgbCoefficients[0] = r;
    rgbCoefficients[1] = g;
    rgbCoefficients[2] = b;
  }

  public RGBCoefficients(double[][] rgbCoefficients) {
    this.rgbCoefficients[0] = rgbCoefficients[0];
    this.rgbCoefficients[1] = rgbCoefficients[1];
    this.rgbCoefficients[2] = rgbCoefficients[2];
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
