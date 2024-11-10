package net.bluevine.chromagica.common;

import static net.bluevine.chromagica.common.TestData.BLUE_COLOR;
import static net.bluevine.chromagica.common.TestData.CYAN_COLOR;
import static net.bluevine.chromagica.common.TestData.TEST_FILAMENT_DATA_MAP;
import static net.bluevine.chromagica.common.TestData.WHITE_COLOR;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import net.bluevine.chromagica.common.model.RGBColor;
import org.junit.jupiter.api.Test;

class ColorUtilTest {
  static final double FLOAT_DELTA = 0.001;

  @Test
  void getColorForSequence_basic() {
    List<String> sequence = List.of("Blue", "White", "Cyan");
    RGBColor expected = new RGBColor(64.49, 141.836, 217.282);
    RGBColor result = ColorUtil.getColorForSequence(sequence, TEST_FILAMENT_DATA_MAP, "White");
    assertAll(
        () -> assertEquals(expected.getR(), result.getR(), FLOAT_DELTA),
        () -> assertEquals(expected.getG(), result.getG(), FLOAT_DELTA),
        () -> assertEquals(expected.getB(), result.getB(), FLOAT_DELTA));
  }

  @Test
  void getColorForSequence_emptySequence() {
    RGBColor result =
        ColorUtil.getColorForSequence(ImmutableList.of(), TEST_FILAMENT_DATA_MAP, "Cyan");
    assertEquals(CYAN_COLOR, result);
  }

  @Test
  void getColorForSequence_reusedResults() {
    List<String> primingSequence = List.of("Blue", "White", "Blue");
    List<String> sequence = List.of("Blue", "White", "White");
    RGBColor expected = new RGBColor(158.809, 202.182, 236.166);

    ColorUtil.getColorForSequence(primingSequence, TEST_FILAMENT_DATA_MAP, "White");

    RGBColor result = ColorUtil.getColorForSequence(sequence, TEST_FILAMENT_DATA_MAP, "White");
    assertAll(
        () -> assertEquals(expected.getR(), result.getR(), FLOAT_DELTA),
        () -> assertEquals(expected.getG(), result.getG(), FLOAT_DELTA),
        () -> assertEquals(expected.getB(), result.getB(), FLOAT_DELTA));
  }

  @Test
  void getDominantColor_basic() {
    Multiset<RGBColor> input = HashMultiset.create(List.of(WHITE_COLOR, BLUE_COLOR, BLUE_COLOR));
    RGBColor result = ColorUtil.getDominantColor(input);

    assertEquals(BLUE_COLOR, result);
  }

  @Test
  void getDominantColor_fallback() {
    RGBColor expected = new RGBColor(0, 0, 0);

    assertEquals(expected, ColorUtil.getDominantColor(null));
  }

  @Test
  void getDominantColors_nullInput() {
    assertTrue(ColorUtil.getDominantColors(null, 10).isEmpty());
  }

  @Test
  void getDominantColors_emptyInput() {
    assertTrue(ColorUtil.getDominantColors(ImmutableList.of(), 10).isEmpty());
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
