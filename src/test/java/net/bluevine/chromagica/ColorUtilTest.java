package net.bluevine.chromagica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ColorUtilTest {
  static final double FLOAT_DELTA = 0.00001;

  @Test
  void calculateDifference() {
    Color color1 = Color.RED;
    Color color2 = Color.WHITE;
    double expected = 11.55123;

    double result = ColorUtil.calculateDifference(color1, color2);

    assertEquals(expected, result, FLOAT_DELTA);
  }

  @Test
  void calculateDifference_zeroDifference() {
    Color color = Color.PINK;

    double result = ColorUtil.calculateDifference(color, color);

    assertEquals(0, result, FLOAT_DELTA);
  }

  @Test
  void getAverageColor() {
    List<Color> colors = List.of(Color.BLUE, Color.YELLOW);
    Color expected = new Color(128, 128, 128);

    Color result = ColorUtil.getAverageColor(colors);

    assertEquals(expected, result);
  }

  @Test
  void getAverageColor_null() {
    Color expected = Color.BLACK;

    Color result = ColorUtil.getAverageColor(null);

    assertEquals(expected, result);
  }

  @Test
  void getAverageColor_empty() {
    Color expected = Color.BLACK;

    Color result = ColorUtil.getAverageColor(new ArrayList<>());

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
