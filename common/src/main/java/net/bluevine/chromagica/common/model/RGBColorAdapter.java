package net.bluevine.chromagica.common.model;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import java.io.IOException;

public class RGBColorAdapter {
  @ToJson
  public int[] toJson(RGBColor color) {
    return new int[] {color.getRInt(), color.getGInt(), color.getBInt()};
  }

  @FromJson
  public RGBColor fromJson(int[] rgb) throws IOException {
    if (rgb == null || rgb.length != 3) {
      throw new IOException("Invalid RGB array");
    }
    return new RGBColor(rgb[0], rgb[1], rgb[2]);
  }
}
