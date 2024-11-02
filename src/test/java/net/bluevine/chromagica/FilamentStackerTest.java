package net.bluevine.chromagica;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import net.bluevine.chromagica.FilamentStacker.FilamentStack;
import net.bluevine.chromagica.model.FilamentData;
import net.bluevine.chromagica.model.RGBCoefficients;
import net.bluevine.chromagica.model.RGBColor;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class FilamentStackerTest {
  private static final RGBColor BLUE_COLOR = new RGBColor(7, 36, 153);
  private static final RGBCoefficients BLUE_COEFFICIENTS =
      new RGBCoefficients(
          new double[] {4.480981432791474, 0.4443952684960717, -6.000013383278259E-4},
          new double[] {24.75142700938747, 0.35721514086022566, 4.944319721599738E-4},
          new double[] {20.889749735873615, 0.9464429696439061, -5.933063972022456E-4});
  private static final FilamentData BLUE_FILAMENT_DATA =
      new FilamentData(BLUE_COLOR, BLUE_COEFFICIENTS, emptyMap());

  private static final RGBColor CYAN_COLOR = new RGBColor(18, 94, 195);
  private static final RGBCoefficients CYAN_COEFFICIENTS =
      new RGBCoefficients(
          new double[] {10.104933275172417, 0.5606841631632997, -9.996605964241558E-4},
          new double[] {45.47346601829285, 0.5641957131366364, -1.5870963339774738E-4},
          new double[] {50.81003367840157, 0.8225173880459974, -4.452842482286347E-4});
  private static final FilamentData CYAN_FILAMENT_DATA =
      new FilamentData(CYAN_COLOR, CYAN_COEFFICIENTS, emptyMap());

  private static final RGBColor WHITE_COLOR = new RGBColor(254, 254, 254);
  private static final RGBCoefficients WHITE_COEFFICIENTS =
      new RGBCoefficients(
          new double[] {64.88128463300333, 0.7721479787432053, -1.4653425210567843E-4},
          new double[] {62.17763968106627, 0.8971743277462471, -6.616349721127205E-4},
          new double[] {60.3418795814688, 0.9280928848575695, -7.244634245743158E-4});
  private static final FilamentData WHITE_FILAMENT_DATA =
      new FilamentData(WHITE_COLOR, WHITE_COEFFICIENTS, emptyMap());

  @Test
  void filamentStacker_nullFilamentData() {
    assertThrows(NullPointerException.class, () -> new FilamentStacker(null, "White", 8));
  }

  @Test
  void filamentStacker_nullBaseFilament() {
    assertThrows(NullPointerException.class, () -> new FilamentStacker(emptyMap(), null, 8));
  }

  @Test
  void filamentStacker_negativeLayers() {
    assertThrows(
        IllegalArgumentException.class, () -> new FilamentStacker(emptyMap(), "White", -1));
  }

  @Test
  void optimizeColorSequence_simple() {
    Map<String, FilamentData> filamentData = ImmutableMap.of("White", WHITE_FILAMENT_DATA);

    FilamentStacker stacker = new FilamentStacker(filamentData, "White", 1);

    FilamentStack result = stacker.optimizeColorSequence(WHITE_COLOR);

    assertEquals(List.of("White"), result.getFilamentSequence());
  }

  @Test
  void optimizeColorSequence_colors() {
    Map<String, FilamentData> filamentData =
        ImmutableMap.of(
            "Blue", BLUE_FILAMENT_DATA, "Cyan", CYAN_FILAMENT_DATA, "White", WHITE_FILAMENT_DATA);

    FilamentStacker stacker = new FilamentStacker(filamentData, "White", 4);

    FilamentStack result = stacker.optimizeColorSequence(new RGBColor(100, 150, 225));

    assertEquals(List.of("White", "Cyan", "Blue", "White"), result.getFilamentSequence());
  }

  @Test
  void optimizeColorSequence_topColor() {
    Map<String, FilamentData> filamentData =
        ImmutableMap.of(
            "Blue", BLUE_FILAMENT_DATA, "Cyan", CYAN_FILAMENT_DATA, "White", WHITE_FILAMENT_DATA);

    FilamentStacker stacker = new FilamentStacker(filamentData, "White", 4);

    FilamentStack result =
        stacker.optimizeColorSequence(new RGBColor(100, 150, 225), ImmutableList.of("Cyan"));

    assertEquals(List.of("White", "Blue", "White", "Cyan"), result.getFilamentSequence());
  }

  @Test
  void optimizeColorSequence_cacheException() throws Exception {
    Map<String, FilamentData> filamentData =
        ImmutableMap.of(
            "Blue", BLUE_FILAMENT_DATA, "Cyan", CYAN_FILAMENT_DATA, "White", WHITE_FILAMENT_DATA);
    try (MockedStatic<CacheBuilder> mockedCacheBuilder = mockStatic(CacheBuilder.class)) {
      Cache<List<String>, RGBColor> mockedCache = mock(Cache.class);
      CacheBuilder<List<String>, RGBColor> cacheBuilder = mock(CacheBuilder.class);
      when(cacheBuilder.maximumSize(anyLong())).thenReturn(cacheBuilder);
      when(cacheBuilder.build()).thenReturn(mockedCache);
      mockedCacheBuilder.when(CacheBuilder::newBuilder).thenReturn(cacheBuilder);

      when(mockedCache.get(anyList(), any()))
          .thenThrow(new ExecutionException(new IllegalArgumentException("Mocked exception")));

      // Manually test if the mock works as expected
      FilamentStacker stacker = new FilamentStacker(filamentData, "White", 4);

      RuntimeException thrown =
          assertThrows(
              RuntimeException.class,
              () -> stacker.optimizeColorSequence(new RGBColor(100, 150, 225)));
      assertInstanceOf(IllegalArgumentException.class, thrown.getCause());
    }
  }
}
