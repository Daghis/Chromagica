package net.bluevine.chromagica.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RGBCoefficients {
  private QuadraticCoefficients[] rgbCoefficients = new QuadraticCoefficients[3];

  public static final RGBCoefficients ZERO =
      new RGBCoefficients(
          new QuadraticCoefficients[] {
            QuadraticCoefficients.ZERO, QuadraticCoefficients.ZERO, QuadraticCoefficients.ZERO
          });

  public RGBCoefficients(double[] r, double[] g, double[] b) {
    rgbCoefficients[0] = new QuadraticCoefficients(r);
  }

  public QuadraticCoefficients getR() {
    return rgbCoefficients[0];
  }

  public void setR(QuadraticCoefficients r) {
    rgbCoefficients[0] = r;
  }

  public QuadraticCoefficients getG() {
    return rgbCoefficients[1];
  }

  public void setG(QuadraticCoefficients g) {
    rgbCoefficients[1] = g;
  }

  public QuadraticCoefficients getB() {
    return rgbCoefficients[2];
  }

  public void setB(QuadraticCoefficients b) {
    rgbCoefficients[2] = b;
  }

  @Data
  @AllArgsConstructor
  public static class QuadraticCoefficients {
    private double a;
    private double b;
    private double c;

    public static final QuadraticCoefficients ZERO = new QuadraticCoefficients(0, 0, 0);

    public QuadraticCoefficients(double[] coeffs) {
      a = coeffs[0];
      b = coeffs[1];
      c = coeffs[2];
    }
  }
}
