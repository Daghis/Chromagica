package net.bluevine.chromagica;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import net.bluevine.chromagica.model.FilamentData;
import net.bluevine.chromagica.model.RGBColor;

public class FilamentDataHandler {
  private FilamentDataHandler() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  private static FilamentData sortFilamentData(FilamentData data) {
    Map<RGBColor, RGBColor> sortedMappings = new TreeMap<>(data.mappings());
    return FilamentData.builder()
        .color(data.color())
        .coefficients(data.coefficients())
        .mappings(sortedMappings)
        .build();
  }

  public static void writeToFile(Map<String, FilamentData> data, Path path) throws IOException {
    // Convert the input map to a TreeMap to ensure the keys are sorted
    Map<String, FilamentData> sortedData = new TreeMap<>();
    for (Map.Entry<String, FilamentData> entry : data.entrySet()) {
      sortedData.put(entry.getKey(), sortFilamentData(entry.getValue()));
    }

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(sortedData);
    try (Writer writer = Files.newBufferedWriter(path)) {
      writer.write(json);
    }
  }

  public static Map<String, FilamentData> readFromFile(Path path) throws IOException {
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, FilamentData>>() {}.getType();
    try (Reader reader = Files.newBufferedReader(path)) {
      return gson.fromJson(reader, type);
    }
  }
}
