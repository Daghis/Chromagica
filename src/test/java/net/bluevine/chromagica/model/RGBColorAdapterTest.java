package net.bluevine.chromagica.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class RGBColorAdapterTest {
  private final RGBColorAdapter rgbColorAdapter = new RGBColorAdapter();

  @Test
  void toJson() {
    RGBColor rgbColor = new RGBColor(100, 150, 200);
    int[] expected = new int[] {100, 150, 200};

    int[] result = rgbColorAdapter.toJson(rgbColor);

    assertArrayEquals(expected, result);
  }

  @Test
  void fromJson() throws IOException {
    int[] colorArray = new int[] {10, 20, 255};
    RGBColor expected = new RGBColor(10, 20, 255);

    RGBColor result = rgbColorAdapter.fromJson(colorArray);

    assertEquals(expected, result);
  }

  @Test
  void fromJson_nullValue() {
    assertThrows(IOException.class, () -> rgbColorAdapter.fromJson(null));
  }

  @Test
  void fromJson_invalidArray() {
    int[] badArray = new int[] {1};

    assertThrows(IOException.class, () -> rgbColorAdapter.fromJson(badArray));
  }
}
