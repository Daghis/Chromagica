package net.bluevine.chromagica.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class RGBCoefficientsAdapterTest {
  RGBCoefficientsAdapter coefficientsAdapter = new RGBCoefficientsAdapter();

  @Test
  void toJson() {
    double[][] expected =
        new double[][] {{3.142, 2.718, 1.618}, {10.2, 25.4, 59.59}, {-15.3, 10005.2, 38378.1}};
    RGBCoefficients coefficients = new RGBCoefficients(expected);

    double[][] result = coefficientsAdapter.toJson(coefficients);
    assertArrayEquals(expected, result);
  }

  @Test
  void fromJson() throws IOException {
    double[][] coeffsArray = new double[][] {{1.2, 3.4, 5.6}, {-2.3, -4.5, 6.7}, {0, 0, 0}};
    RGBCoefficients expectedCoefficients = new RGBCoefficients(coeffsArray);

    RGBCoefficients result = coefficientsAdapter.fromJson(coeffsArray);

    assertEquals(expectedCoefficients, result);
  }

  @Test
  void fromJson_nullArgument() {
    assertThrows(IOException.class, () -> coefficientsAdapter.fromJson(null));
  }

  @Test
  void fromJson_badArray() {
    double[][] coeffsArray = new double[][] {{0, 0, 0}, {1, 1, 1}};

    assertThrows(IOException.class, () -> coefficientsAdapter.fromJson(coeffsArray));
  }

  @Test
  void fromJson_badArray_row1() {
    double[][] coeffsArray = new double[][] {{1, 2}, {0, 0, 0}, {0, 0, 0}};

    assertThrows(IOException.class, () -> coefficientsAdapter.fromJson(coeffsArray));
  }

  @Test
  void fromJson_badArray_row2() {
    double[][] coeffsArray = new double[][] {{0, 0, 0}, {1, 2}, {0, 0, 0}};

    assertThrows(IOException.class, () -> coefficientsAdapter.fromJson(coeffsArray));
  }

  @Test
  void fromJson_badArray_row3() {
    double[][] coeffsArray = new double[][] {{0, 0, 0}, {0, 0, 0}, {1, 2}};

    assertThrows(IOException.class, () -> coefficientsAdapter.fromJson(coeffsArray));
  }
}
