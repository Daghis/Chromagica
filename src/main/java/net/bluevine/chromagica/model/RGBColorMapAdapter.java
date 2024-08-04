package net.bluevine.chromagica.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.ToJson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class RGBColorMapAdapter {
  @ToJson
  public void toJson(
      JsonWriter writer, Map<RGBColor, RGBColor> mappings, JsonAdapter<int[]> colorAdapter)
      throws IOException {
    TreeMap<RGBColor, RGBColor> sortedMap = new TreeMap<>(mappings);

    writer.beginArray();
    for (Map.Entry<RGBColor, RGBColor> entry : sortedMap.entrySet()) {
      writer.beginArray();
      colorAdapter.toJson(writer, entry.getKey().getRgb());
      colorAdapter.toJson(writer, entry.getValue().getRgb());
      writer.endArray();
    }
    writer.endArray();
  }

  @FromJson
  public Map<RGBColor, RGBColor> fromJson(JsonReader reader, JsonAdapter<int[]> colorAdapter)
      throws IOException {
    Map<RGBColor, RGBColor> mappings = new HashMap<>();
    reader.beginArray();
    while (reader.hasNext()) {
      reader.beginArray();
      int[] keyRgb = checkNotNull(colorAdapter.fromJson(reader));
      int[] valueRgb = checkNotNull(colorAdapter.fromJson(reader));
      RGBColor key = new RGBColor(keyRgb[0], keyRgb[1], keyRgb[2]);
      RGBColor value = new RGBColor(valueRgb[0], valueRgb[1], valueRgb[2]);
      mappings.put(key, value);
      reader.endArray();
    }
    reader.endArray();
    return mappings;
  }
}