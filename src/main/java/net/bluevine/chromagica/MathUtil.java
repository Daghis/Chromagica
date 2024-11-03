package net.bluevine.chromagica;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;
import org.apache.commons.math3.util.Combinations;

public class MathUtil {
  private MathUtil() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static <T> ImmutableSet<ImmutableSet<T>> getAllCombinations(
      Collection<T> items, int countPerSet) {

    // Input validation
    if (countPerSet < 0 || countPerSet > items.size()) {
      return ImmutableSet.of(); // Return an empty set when the count is invalid
    }
    if (countPerSet == 0) {
      return ImmutableSet.of(ImmutableSet.of()); // Return a set containing an empty set
    }

    List<T> itemsList = new ArrayList<>(items);
    return StreamSupport.stream(new Combinations(items.size(), countPerSet).spliterator(), true)
        .map(indices -> Arrays.stream(indices).mapToObj(itemsList::get).collect(toImmutableSet()))
        .collect(toImmutableSet());
  }

  public static double applyQuadraticCoefficients(double x, double[] coefficients) {
    return coefficients[2] * x * x + coefficients[1] * x + coefficients[0];
  }
}
