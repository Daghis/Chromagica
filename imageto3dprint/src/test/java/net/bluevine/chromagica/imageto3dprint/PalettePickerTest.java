package net.bluevine.chromagica.imageto3dprint;

import static net.bluevine.chromagica.imageto3dprint.TestData.TEST_FILAMENT_DATA_MAP;
import static net.bluevine.chromagica.imageto3dprint.TestData.WHITE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import net.bluevine.chromagica.imageto3dprint.PalettePicker.Palette;
import org.junit.jupiter.api.Test;

class PalettePickerTest {
  @Test
  void getBestPalettes_simple() {
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    image.setRGB(0, 0, Color.WHITE.getRGB());

    PalettePicker picker =
        new PalettePicker(
            TEST_FILAMENT_DATA_MAP,
            2,
            1,
            WHITE_NAME,
            List.of(),
            List.of(),
            image);

    ImmutableList<Palette> palettes = picker.getBestPalettes(2);

    assertEquals(2, palettes.size());
    assertEquals(ImmutableList.of("Blue", "White"),
        ImmutableList.sortedCopyOf(palettes.get(0).getFilaments()));
    assertEquals(ImmutableList.of("Cyan", "White"),
        ImmutableList.sortedCopyOf(palettes.get(1).getFilaments()));
  }
}
