package net.bluevine.chromagica;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import net.bluevine.chromagica.data.FilamentData;

public class FilamentDataHandler {

  public static void writeToFile(Map<String, FilamentData> data, File file) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(data);
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(json);
    }
  }
}
