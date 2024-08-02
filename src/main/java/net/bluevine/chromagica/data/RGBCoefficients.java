package net.bluevine.chromagica.data;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class RGBCoefficients {
  public static final RGBCoefficients ZERO =
      RGBCoefficients.create(
          QuadraticCoefficients.ZERO, QuadraticCoefficients.ZERO, QuadraticCoefficients.ZERO);

  public abstract QuadraticCoefficients r();

  public abstract QuadraticCoefficients g();

  public abstract QuadraticCoefficients b();

  public static RGBCoefficients create(
      QuadraticCoefficients r, QuadraticCoefficients g, QuadraticCoefficients b) {
    return new AutoValue_RGBCoefficients(r, g, b);
  }

  public static RGBCoefficients create(
      double[] redCoefficients, double[] greenCoefficients, double[] blueCoefficients) {
    return new AutoValue_RGBCoefficients(
        QuadraticCoefficients.create(redCoefficients),
        QuadraticCoefficients.create(greenCoefficients),
        QuadraticCoefficients.create(blueCoefficients));
  }

  @AutoValue
  public abstract static class QuadraticCoefficients {
    public static final QuadraticCoefficients ZERO = create(new double[] {0, 0, 0});

    public abstract double a();

    public abstract double b();

    public abstract double c();

    public static QuadraticCoefficients create(double[] coefficients) {
      return new AutoValue_RGBCoefficients_QuadraticCoefficients(
          coefficients[2], coefficients[1], coefficients[0]);
    }
  }
}
