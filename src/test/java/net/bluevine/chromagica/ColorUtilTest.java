package net.bluevine.chromagica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import net.bluevine.chromagica.model.RGBColor;
import org.junit.jupiter.api.Test;

class ColorUtilTest {
  static final double FLOAT_DELTA = 0.00001;

  @Test
  void calculateDifference() {
    RGBColor color1 = RGBColor.create(255, 0, 0);
    RGBColor color2 = RGBColor.create(255, 255, 255);
    double expected = 11.55123;

    double result = ColorUtil.calculateDifference(color1, color2);

    assertEquals(expected, result, FLOAT_DELTA);
  }

  @Test
  void calculateDifference_zeroDifference() {
    RGBColor color = RGBColor.create(255, 180, 240);

    double result = ColorUtil.calculateDifference(color, color);

    assertEquals(0, result, FLOAT_DELTA);
  }

  @Test
  void getAverageColor() {
    List<RGBColor> colors = List.of(RGBColor.create(0, 0, 255), RGBColor.create(255, 255, 0));
    RGBColor expected = RGBColor.create(128, 128, 128);

    RGBColor result = ColorUtil.getAverageColor(colors);

    assertEquals(expected, result);
  }

  @Test
  void getAverageColor_null() {
    RGBColor expected = RGBColor.create(0, 0, 0);

    RGBColor result = ColorUtil.getAverageColor(null);

    assertEquals(expected, result);
  }

  @Test
  void getAverageColor_empty() {
    RGBColor expected = RGBColor.create(0, 0, 0);

    RGBColor result = ColorUtil.getAverageColor(new ArrayList<>());

    assertEquals(expected, result);
  }

  @Test
  void colorUtil_constructorIsPrivate() throws Exception {
    Constructor<ColorUtil> constructor = ColorUtil.class.getDeclaredConstructor();
    assertFalse(constructor.canAccess(null));
  }

  @Test
  void colorUtil_constructorException() throws Exception {
    Constructor<ColorUtil> constructor = ColorUtil.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
    assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
  }
}
