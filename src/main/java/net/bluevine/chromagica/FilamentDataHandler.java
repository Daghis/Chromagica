package net.bluevine.chromagica;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import net.bluevine.chromagica.model.FilamentData;
import net.bluevine.chromagica.model.RGBColorMapAdapter;

public class FilamentDataHandler {
  private FilamentDataHandler() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static void writeToFile(Map<String, FilamentData> data, Path path) throws IOException {
    // Convert the input map to a TreeMap to ensure the keys are sorted
    Map<String, FilamentData> sortedData = new TreeMap<>(data);

    Moshi moshi = new Moshi.Builder().add(new RGBColorMapAdapter()).build();
    Type type = Types.newParameterizedType(Map.class, String.class, FilamentData.class);
    JsonAdapter<Map<String, FilamentData>> adapter = moshi.adapter(type);
    String json = adapter.indent("  ").toJson(sortedData);
    Files.writeString(path, json);
  }

  public static Map<String, FilamentData> readFromFile(Path path) throws IOException {
    Moshi moshi = new Moshi.Builder().add(new RGBColorMapAdapter()).build();
    Type type = Types.newParameterizedType(Map.class, String.class, FilamentData.class);
    JsonAdapter<Map<String, FilamentData>> adapter = moshi.adapter(type);

    return adapter.fromJson(Files.readString(path));
  }
}
