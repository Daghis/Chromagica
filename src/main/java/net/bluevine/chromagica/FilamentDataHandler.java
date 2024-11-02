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
import net.bluevine.chromagica.model.RGBCoefficientsAdapter;
import net.bluevine.chromagica.model.RGBColorAdapter;
import net.bluevine.chromagica.model.RGBColorMapAdapter;

public class FilamentDataHandler {
  private FilamentDataHandler() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  private static final Moshi MOSHI =
      new Moshi.Builder()
          .add(new RGBColorAdapter())
          .add(new RGBCoefficientsAdapter())
          .add(new RGBColorMapAdapter())
          .build();

  public static void writeToFile(Map<String, FilamentData> data, Path path) throws IOException {
    // Convert the input map to a TreeMap to ensure the keys are sorted
    Map<String, FilamentData> sortedData = new TreeMap<>(data);

    Type type = Types.newParameterizedType(Map.class, String.class, FilamentData.class);
    JsonAdapter<Map<String, FilamentData>> adapter = MOSHI.adapter(type);
    String json = adapter.indent("  ").toJson(sortedData);
    Files.writeString(path, json);
  }

  public static Map<String, FilamentData> readFromFile(Path path) throws IOException {
    Type type = Types.newParameterizedType(Map.class, String.class, FilamentData.class);
    JsonAdapter<Map<String, FilamentData>> adapter = MOSHI.adapter(type);

    Map<String, FilamentData> caseInsensitiveMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    Map<String, FilamentData> filamentDataMap = adapter.fromJson(Files.readString(path));
    if (filamentDataMap != null) {
      caseInsensitiveMap.putAll(filamentDataMap);
    }
    return caseInsensitiveMap;
  }
}
