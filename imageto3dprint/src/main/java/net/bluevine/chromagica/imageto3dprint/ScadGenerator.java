package net.bluevine.chromagica.imageto3dprint;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.stream.Collectors.toList;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Value;

public class ScadGenerator {
  private final double pixelSize;
  private final double layerHeight;
  private final double baseHeight;
  Table<Integer, Integer, List<String>> filamentStacks;

  public ScadGenerator(
      double pixelSize,
      double layerHeight,
      double baseHeight,
      Table<Integer, Integer, List<String>> filamentStacks) {
    this.pixelSize = pixelSize;
    this.layerHeight = layerHeight;
    this.baseHeight = baseHeight;
    this.filamentStacks = ImmutableTable.copyOf(filamentStacks);
  }

  @Value
  @AllArgsConstructor
  public static class Coordinates {
    int x, y, z;
  }

  public ImmutableMap<String, String> generateScadContent() {
    Map<String, Set<Coordinates>> filamentMap = new HashMap<>();
    for (Cell<Integer, Integer, List<String>> cell : filamentStacks.cellSet()) {
      List<String> filamentStack = cell.getValue();
      for (int z = 0; z < filamentStack.size(); z++) {
        filamentMap
            .computeIfAbsent(filamentStack.get(z), k -> new HashSet<>())
            .add(new Coordinates(cell.getColumnKey(), cell.getRowKey(), z));
      }
    }

    return filamentMap.entrySet().stream()
        .collect(
            toImmutableMap(
                Entry::getKey, entry -> generateScadContent(entry.getKey(), entry.getValue())));
  }

  private String generateScadContent(String filament, Set<Coordinates> pixelCoordinates) {
    Map<Integer, List<String>> cubesByZ = new HashMap<>();

    pixelCoordinates.forEach(
        coord ->
            cubesByZ
                .computeIfAbsent(coord.x, x -> new ArrayList<>())
                .add(formatCube(coord.getX(), coord.getY(), coord.getZ())));

    List<String> zUnions =
        cubesByZ.values().stream().map(ScadGenerator::createUnion).collect(toList());

    String finalUnion = createUnion(zUnions);

    return String.format("// Filament %s%n%s%n", filament, finalUnion);
  }

  private String formatCube(int x, int y, int z) {
    return String.format(
        "translate([%.2f, %.2f, %.2f]) cube([%.2f, %.2f, %.2f]);",
        x * pixelSize,
        y * pixelSize,
        z * layerHeight + baseHeight,
        pixelSize,
        pixelSize,
        layerHeight);
  }

  private static String createUnion(List<String> items) {
    if (items.size() == 1) {
      return items.get(0);
    }
    int mid = items.size() / 2;
    return String.format(
        "union() {%n  %s%n  %s%n}",
        createUnion(items.subList(0, mid)), createUnion(items.subList(mid, items.size())));
  }
}
