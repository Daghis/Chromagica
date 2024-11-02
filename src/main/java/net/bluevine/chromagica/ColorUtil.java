package net.bluevine.chromagica;

import static java.util.Comparator.comparingInt;
import static net.bluevine.chromagica.MathUtil.applyQuadraticCoefficients;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Value;
import net.bluevine.chromagica.model.FilamentData;
import net.bluevine.chromagica.model.RGBCoefficients;
import net.bluevine.chromagica.model.RGBColor;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

public class ColorUtil {
  private static final int MAX_CLUSTERER_ITERATIONS = 100;

  private ColorUtil() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static Multiset<RGBColor> getDominantColors(Collection<RGBColor> colors, int count) {
    Multiset<RGBColor> dominantColors = HashMultiset.create();

    if (colors == null || colors.isEmpty()) {
      return dominantColors;
    }

    List<DoublePoint> points =
        colors.stream()
            .map(color -> new DoublePoint(new double[] {color.getR(), color.getG(), color.getB()}))
            .collect(Collectors.toList());

    KMeansPlusPlusClusterer<DoublePoint> clusterer =
        new KMeansPlusPlusClusterer<>(count, MAX_CLUSTERER_ITERATIONS);
    List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(points);

    clusters.forEach(
        cluster -> {
          double[] centroid = cluster.getCenter().getPoint();
          RGBColor dominantColor = new RGBColor(centroid[0], centroid[1], centroid[2]);
          dominantColors.add(dominantColor, cluster.getPoints().size());
        });

    return dominantColors;
  }

  public static RGBColor getDominantColor(Collection<RGBColor> colors) {
    return getDominantColors(colors, 2).entrySet().stream()
        .max(comparingInt(Entry::getCount))
        .map(Entry::getElement)
        .orElse(new RGBColor(0, 0, 0));
  }

  @Value
  private static class ColorNode {
    RGBColor color;
    ConcurrentHashMap<String, ColorNode> children = new ConcurrentHashMap<>();
  }

  private static final ConcurrentHashMap<String, ColorNode> root = new ConcurrentHashMap<>();

  public static RGBColor getColorForSequence(
      List<String> sequence, Map<String, FilamentData> filamentData, String baseFilament) {
    // Start with the baseFilament.
    ColorNode currentNode =
        root.computeIfAbsent(
            baseFilament, x -> new ColorNode(filamentData.get(baseFilament).getColor()));

    RGBColor color = currentNode.getColor();
    for (String filament : sequence) {
      ColorNode child = currentNode.getChildren().get(filament);
      if (child != null) {
        color = child.getColor();
        currentNode = child;
        continue;
      }

      // "Add" filament to color.
      RGBCoefficients coefficients = filamentData.get(filament).getCoefficients();
      color =
          new RGBColor(
              applyQuadraticCoefficients(color.getR(), coefficients.getR()),
              applyQuadraticCoefficients(color.getG(), coefficients.getG()),
              applyQuadraticCoefficients(color.getB(), coefficients.getB()));
      child = new ColorNode(color);

      currentNode.getChildren().put(filament, child);

      currentNode = child;
    }

    return color;
  }
}
