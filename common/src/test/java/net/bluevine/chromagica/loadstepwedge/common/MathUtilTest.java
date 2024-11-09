package net.bluevine.chromagica.loadstepwedge.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableSet;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.bluevine.chromagica.common.MathUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MathUtilTest {

  @Test
  @DisplayName("Test combinations with a normal set of items")
  void testGetAllCombinationsNormal() {
    List<String> items = Arrays.asList("A", "B", "C");
    int countPerSet = 2;

    Set<ImmutableSet<String>> expectedCombinations =
        ImmutableSet.of(
            ImmutableSet.of("A", "B"), ImmutableSet.of("A", "C"), ImmutableSet.of("B", "C"));

    ImmutableSet<ImmutableSet<String>> result = MathUtil.getAllCombinations(items, countPerSet);

    assertEquals(expectedCombinations, result, "Should return all 2-combinations of A, B, C");
  }

  @Test
  @DisplayName("Test combinations with countPerSet equal to size of items")
  void testGetAllCombinationsFullSize() {
    List<String> items = Arrays.asList("X", "Y", "Z");
    int countPerSet = 3;

    Set<ImmutableSet<String>> expectedCombinations =
        ImmutableSet.of(ImmutableSet.of("X", "Y", "Z"));

    ImmutableSet<ImmutableSet<String>> result = MathUtil.getAllCombinations(items, countPerSet);

    assertEquals(expectedCombinations, result, "Should return the only combination with all items");
  }

  @Test
  @DisplayName("Test combinations with countPerSet = 1")
  void testGetAllCombinationsSingleItem() {
    List<String> items = Arrays.asList("M", "N", "O");
    int countPerSet = 1;

    Set<ImmutableSet<String>> expectedCombinations =
        ImmutableSet.of(ImmutableSet.of("M"), ImmutableSet.of("N"), ImmutableSet.of("O"));

    ImmutableSet<ImmutableSet<String>> result = MathUtil.getAllCombinations(items, countPerSet);

    assertEquals(expectedCombinations, result, "Should return all single-item combinations");
  }

  @Test
  @DisplayName("Test combinations with countPerSet = 0")
  void testGetAllCombinationsZeroCount() {
    List<String> items = Arrays.asList("A", "B", "C");
    int countPerSet = 0;

    ImmutableSet<ImmutableSet<String>> result = MathUtil.getAllCombinations(items, countPerSet);

    // Expected output should be an empty set of combinations
    Set<ImmutableSet<String>> expectedCombinations = ImmutableSet.of(ImmutableSet.of());

    assertEquals(
        expectedCombinations, result, "Should return a set containing an empty combination");
  }

  @Test
  @DisplayName("Test combinations with countPerSet greater than size of items")
  void testGetAllCombinationsInvalidCount() {
    List<String> items = Arrays.asList("P", "Q");
    int countPerSet = 3;

    ImmutableSet<ImmutableSet<String>> result = MathUtil.getAllCombinations(items, countPerSet);

    // Expected output should be an empty set as no combinations are possible
    Set<ImmutableSet<String>> expectedCombinations = ImmutableSet.of();

    assertEquals(
        expectedCombinations, result, "Should return an empty set as no valid combinations exist");
  }

  @Test
  @DisplayName("Test combinations with an empty set of items")
  void testGetAllCombinationsEmptyItems() {
    List<String> items = Collections.emptyList();
    int countPerSet = 2;

    ImmutableSet<ImmutableSet<String>> result = MathUtil.getAllCombinations(items, countPerSet);

    // Expected output should be an empty set as no combinations are possible
    Set<ImmutableSet<String>> expectedCombinations = ImmutableSet.of();

    assertEquals(expectedCombinations, result, "Should return an empty set for empty input list");
  }

  @Test
  @DisplayName("Test combinations with a countPerSet of 1 for a single-item list")
  void testGetAllCombinationsSingleItemList() {
    List<String> items = Collections.singletonList("Z");
    int countPerSet = 1;

    Set<ImmutableSet<String>> expectedCombinations = ImmutableSet.of(ImmutableSet.of("Z"));

    ImmutableSet<ImmutableSet<String>> result = MathUtil.getAllCombinations(items, countPerSet);

    assertEquals(expectedCombinations, result, "Should return the only single-item combination");
  }

  @Test
  @DisplayName("Test combinations with a negative countPerSet")
  void testGetAllCombinationsNegativeCount() {
    List<String> items = Arrays.asList("A", "B", "C");
    int countPerSet = -1;

    ImmutableSet<ImmutableSet<String>> result = MathUtil.getAllCombinations(items, countPerSet);

    // Expected output should be an empty set since the count is invalid
    Set<ImmutableSet<String>> expectedCombinations = ImmutableSet.of();

    assertEquals(
        expectedCombinations, result, "Should return an empty set for negative countPerSet");
  }

  @Test
  void applyQuadraticCoefficients() {
    double[] coefficients = new double[] {10.0, -2.5, 0.3};
    double x = 123.4;
    double expected = 4269.768;

    assertEquals(expected, MathUtil.applyQuadraticCoefficients(x, coefficients), 0.00001);
  }

  @Test
  void privateConstructor_shouldThrowUnsupportedOperationException() throws Exception {
    // Obtain the private constructor of FilamentDataHandler
    Constructor<MathUtil> constructor = MathUtil.class.getDeclaredConstructor();

    // Make the private constructor accessible
    constructor.setAccessible(true);

    // Attempt to instantiate FilamentDataHandler and expect an InvocationTargetException
    InvocationTargetException invocationException =
        assertThrows(
            InvocationTargetException.class,
            constructor::newInstance,
            "Expected InvocationTargetException when invoking private constructor");

    // Assert that the cause of the InvocationTargetException is UnsupportedOperationException
    Throwable cause = invocationException.getCause();
    assertInstanceOf(UnsupportedOperationException.class, cause);
  }
}
