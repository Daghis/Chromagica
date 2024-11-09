package net.bluevine.chromagica.common.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RGBColorTest {

  @Test
  @DisplayName("toString basic test")
  void testToString() {
    RGBColor rgbColor = new RGBColor(255, 115.4, 23.14);

    String result = rgbColor.toString();
    assertEquals("RGBColor(rgb=#ff7317 [255.00, 115.40, 23.14])", result);
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Construct RGBColor with double values")
    void testRGBColorDoubleConstructor() {
      RGBColor color = new RGBColor(255.0, 128.0, 64.0);
      assertAll("RGB components",
          () -> assertEquals(255.0, color.getR()),
          () -> assertEquals(128.0, color.getG()),
          () -> assertEquals(64.0, color.getB())
      );
    }

    @Test
    @DisplayName("Construct RGBColor with Color object")
    void testRGBColorColorConstructor() {
      Color awtColor = new Color(100, 150, 200);
      RGBColor color = new RGBColor(awtColor);
      assertAll("RGB components",
          () -> assertEquals(100.0, color.getR()),
          () -> assertEquals(150.0, color.getG()),
          () -> assertEquals(200.0, color.getB())
      );
    }

    @Test
    @DisplayName("Construct RGBColor with integer")
    void testRGBColorIntConstructor() {
      // Color: Red=0x12, Green=0x34, Blue=0x56
      int colorInt = (0x12 << 16) | (0x34 << 8) | 0x56;
      RGBColor color = new RGBColor(colorInt);
      assertAll("RGB components",
          () -> assertEquals(18.0, color.getR()),
          () -> assertEquals(52.0, color.getG()),
          () -> assertEquals(86.0, color.getB())
      );
    }
  }

  @Nested
  @DisplayName("Getter Methods Tests")
  class GetterMethodsTests {

    RGBColor color = new RGBColor(255.0, 128.0, 64.0);

    @Test
    @DisplayName("Test getR method")
    void testGetR() {
      assertEquals(255.0, color.getR());
    }

    @Test
    @DisplayName("Test getG method")
    void testGetG() {
      assertEquals(128.0, color.getG());
    }

    @Test
    @DisplayName("Test getB method")
    void testGetB() {
      assertEquals(64.0, color.getB());
    }

    @Test
    @DisplayName("Test getRInt method")
    void testGetRInt() {
      assertEquals(255, color.getRInt());
    }

    @Test
    @DisplayName("Test getGInt method")
    void testGetGInt() {
      assertEquals(128, color.getGInt());
    }

    @Test
    @DisplayName("Test getBInt method")
    void testGetBInt() {
      assertEquals(64, color.getBInt());
    }
  }

  @Nested
  @DisplayName("RGB Combination Tests")
  class RGBCombinationTests {

    @Test
    @DisplayName("Test getRGB method")
    void testGetRGB() {
      RGBColor color = new RGBColor(255.0, 128.0, 64.0);
      int expectedRGB = (255 << 24) | (255 << 16) | (128 << 8) | 64;
      assertEquals(expectedRGB, color.getRGB());
    }

    @Test
    @DisplayName("Test getIntArray method")
    void testGetIntArray() {
      RGBColor color = new RGBColor(10.0, 20.0, 30.0);
      int[] expectedArray = {10, 20, 30};
      assertArrayEquals(expectedArray, color.getIntArray());
    }
  }

  @Nested
  @DisplayName("Lab Distance Tests")
  class LabDistanceTests {

    @Test
    @DisplayName("Test computeLabDistance between identical colors")
    void testComputeLabDistanceIdentical() {
      RGBColor color1 = new RGBColor(100.0, 150.0, 200.0);
      RGBColor color2 = new RGBColor(100.0, 150.0, 200.0);
      assertEquals(0.0, color1.computeLabDistance(color2), 1e-6);
    }

    @Test
    @DisplayName("Test computeLabDistance between different colors")
    void testComputeLabDistanceDifferent() {
      RGBColor color1 = new RGBColor(0.0, 0.0, 0.0); // Black
      RGBColor color2 = new RGBColor(255.0, 255.0, 255.0); // White
      double distance = color1.computeLabDistance(color2);
      // The exact distance may vary, but it should be greater than 0
      assertTrue(distance > 0.0);
    }

    @Test
    @DisplayName("Test computeLabDistance with known values")
    void testComputeLabDistanceKnown() {
      // These values are approximate and depend on the Lab conversion implementation
      RGBColor color1 = new RGBColor(255.0, 0.0, 0.0); // Red
      RGBColor color2 = new RGBColor(0.0, 255.0, 0.0); // Green
      double distance = color1.computeLabDistance(color2);
      // Updated expected distance based on standard Lab conversion
      double expectedDistance = 170.58;
      assertEquals(expectedDistance, distance, 0.1, "Lab distance between pure red and pure green should be approximately 170.58");
    }
  }

  @Nested
  @DisplayName("CompareTo Method Tests")
  class CompareToMethodTests {

    @Test
    @DisplayName("Compare identical colors")
    void testCompareToIdentical() {
      RGBColor color1 = new RGBColor(100.0, 150.0, 200.0);
      RGBColor color2 = new RGBColor(100.0, 150.0, 200.0);
      assertEquals(0, color1.compareTo(color2));
    }

    @Test
    @DisplayName("Compare colors with different R")
    void testCompareToDifferentR() {
      RGBColor color1 = new RGBColor(100.0, 150.0, 200.0);
      RGBColor color2 = new RGBColor(101.0, 150.0, 200.0);
      assertTrue(color1.compareTo(color2) < 0);
      assertTrue(color2.compareTo(color1) > 0);
    }

    @Test
    @DisplayName("Compare colors with same R but different G")
    void testCompareToSameRDifferentG() {
      RGBColor color1 = new RGBColor(100.0, 150.0, 200.0);
      RGBColor color2 = new RGBColor(100.0, 151.0, 200.0);
      assertTrue(color1.compareTo(color2) < 0);
      assertTrue(color2.compareTo(color1) > 0);
    }

    @Test
    @DisplayName("Compare colors with same R and G but different B")
    void testCompareToSameRGSameB() {
      RGBColor color1 = new RGBColor(100.0, 150.0, 200.0);
      RGBColor color2 = new RGBColor(100.0, 150.0, 201.0);
      assertTrue(color1.compareTo(color2) < 0);
      assertTrue(color2.compareTo(color1) > 0);
    }
  }

  @Nested
  @DisplayName("Clamping Tests")
  class ClampingTests {

    @Test
    @DisplayName("Test clamping for values below 0")
    void testClampingBelowZero() {
      RGBColor color = new RGBColor(-10.0, -20.0, -30.0);
      assertAll("Clamped RGB components",
          () -> assertEquals(0, color.getRInt()),
          () -> assertEquals(0, color.getGInt()),
          () -> assertEquals(0, color.getBInt())
      );
    }

    @Test
    @DisplayName("Test clamping for values above 255")
    void testClampingAbove255() {
      RGBColor color = new RGBColor(300.0, 400.0, 500.0);
      assertAll("Clamped RGB components",
          () -> assertEquals(255, color.getRInt()),
          () -> assertEquals(255, color.getGInt()),
          () -> assertEquals(255, color.getBInt())
      );
    }

    @Test
    @DisplayName("Test clamping for values within range")
    void testClampingWithinRange() {
      RGBColor color = new RGBColor(100.0, 150.0, 200.0);
      assertAll("Clamped RGB components",
          () -> assertEquals(100, color.getRInt()),
          () -> assertEquals(150, color.getGInt()),
          () -> assertEquals(200, color.getBInt())
      );
    }
  }

  @Nested
  @DisplayName("Edge Case Tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("Test with all components zero (Black)")
    void testBlackColor() {
      RGBColor black = new RGBColor(0.0, 0.0, 0.0);
      assertAll("Black color components",
          () -> assertEquals(0.0, black.getR()),
          () -> assertEquals(0.0, black.getG()),
          () -> assertEquals(0.0, black.getB()),
          () -> assertEquals(0xFF000000, black.getRGB())
      );
    }

    @Test
    @DisplayName("Test with all components maximum (White)")
    void testWhiteColor() {
      RGBColor white = new RGBColor(255.0, 255.0, 255.0);
      assertAll("White color components",
          () -> assertEquals(255.0, white.getR()),
          () -> assertEquals(255.0, white.getG()),
          () -> assertEquals(255.0, white.getB()),
          () -> assertEquals(0xFFFFFFFF, white.getRGB())
      );
    }

    @Test
    @DisplayName("Test with mixed components")
    void testMixedColor() {
      RGBColor color = new RGBColor(123.45, 67.89, 250.12);
      assertAll("Mixed color components",
          () -> assertEquals(123.45, color.getR(), 1e-2),
          () -> assertEquals(67.89, color.getG(), 1e-2),
          () -> assertEquals(250.12, color.getB(), 1e-2)
      );
    }

    @Test
    @DisplayName("Test compareTo with extreme values")
    void testCompareToExtremeValues() {
      RGBColor minColor = new RGBColor(0.0, 0.0, 0.0);
      RGBColor maxColor = new RGBColor(255.0, 255.0, 255.0);
      assertTrue(minColor.compareTo(maxColor) < 0);
      assertTrue(maxColor.compareTo(minColor) > 0);
    }
  }
}