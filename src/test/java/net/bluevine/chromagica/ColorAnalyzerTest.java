package net.bluevine.chromagica;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.Iterables;
import java.awt.Color;
import java.net.URL;
import java.util.List;
import org.junit.jupiter.api.Test;

class ColorAnalyzerTest {
  @Test
  void analyze_simpleImage() throws Exception {
    URL testImagePath = getClass().getResource("/Orange-1x1.png");
    assertNotNull(testImagePath);

    ColorAnalyzer analyzer =
        ColorAnalyzer.analyze(testImagePath.getFile(), List.of("Orange"), 1, 1);
    assertEquals("Orange", Iterables.getOnlyElement(analyzer.filamentData.keySet()));
    assertEquals(
        new Color(255, 100, 25), Iterables.getOnlyElement(analyzer.filamentData.values()).color());
  }

  @Test
  void analyze_multiplePixelsPerChip_sameColor() throws Exception {
    URL testImagePath = getClass().getResource("/Yellow-4x3.png");
    assertNotNull(testImagePath);

    ColorAnalyzer analyzer =
        ColorAnalyzer.analyze(testImagePath.getFile(), List.of("Yellow"), 1, 1);
    assertEquals("Yellow", Iterables.getOnlyElement(analyzer.filamentData.keySet()));
    assertEquals(
        new Color(255, 255, 100), Iterables.getOnlyElement(analyzer.filamentData.values()).color());

  }

  @Test
  void analyze_multiplePixelsPerChip_averagedColor() throws Exception {
    URL testImagePath = getClass().getResource("/Magenta+Green-4x3.png");
    assertNotNull(testImagePath);

    ColorAnalyzer analyzer =
        ColorAnalyzer.analyze(testImagePath.getFile(), List.of("Average"), 1, 1);
    assertEquals("Average", Iterables.getOnlyElement(analyzer.filamentData.keySet()));
    assertEquals(
        new Color(128, 153, 153), Iterables.getOnlyElement(analyzer.filamentData.values()).color());

  }

}
