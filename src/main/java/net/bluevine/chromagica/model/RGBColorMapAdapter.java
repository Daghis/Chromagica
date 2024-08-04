package net.bluevine.chromagica.model;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.ToJson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RGBColorMapAdapter {
  @ToJson
  public void toJson(
      JsonWriter writer, Map<RGBColor, RGBColor> mappings, JsonAdapter<RGBColor> colorAdapter)
      throws IOException {
    writer.beginArray();
    for (Map.Entry<RGBColor, RGBColor> entry : mappings.entrySet()) {
      writer.beginObject();
      writer.name("key");
      colorAdapter.toJson(writer, entry.getKey());
      writer.name("value");
      colorAdapter.toJson(writer, entry.getValue());
      writer.endObject();
    }
    writer.endArray();
  }

  @FromJson
  public Map<RGBColor, RGBColor> fromJson(JsonReader reader, JsonAdapter<RGBColor> colorAdapter)
      throws IOException {
    Map<RGBColor, RGBColor> mappings = new HashMap<>();
    reader.beginArray();
    while (reader.hasNext()) {
      reader.beginObject();
      RGBColor key = null;
      RGBColor value = null;
      while (reader.hasNext()) {
        switch (reader.selectName(JsonReader.Options.of("key", "value"))) {
          case 0:
            key = colorAdapter.fromJson(reader);
            break;
          case 1:
            value = colorAdapter.fromJson(reader);
            break;
          default:
            reader.skipName();
            reader.skipValue();
            break;
        }
      }
      mappings.put(key, value);
      reader.endObject();
    }
    reader.endArray();
    return mappings;
  }
}
