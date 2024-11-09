package net.bluevine.chromagica.common.model;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import java.io.IOException;

public class RGBCoefficientsAdapter {
  @ToJson
  public double[][] toJson(RGBCoefficients coefficients) {
    return coefficients.getRgbCoefficients();
  }

  @FromJson
  public RGBCoefficients fromJson(double[][] coeffs) throws IOException {
    if (coeffs == null
        || coeffs.length != 3
        || coeffs[0].length != 3
        || coeffs[1].length != 3
        || coeffs[2].length != 3) {
      throw new IOException("Invalid RGB array");
    }
    return new RGBCoefficients(coeffs[0], coeffs[1], coeffs[2]);
  }
}
