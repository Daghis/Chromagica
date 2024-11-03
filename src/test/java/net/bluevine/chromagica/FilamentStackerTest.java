package net.bluevine.chromagica;

import static java.util.Collections.emptyMap;
import static net.bluevine.chromagica.TestData.TEST_FILAMENT_DATA_MAP;
import static net.bluevine.chromagica.TestData.WHITE_COLOR;
import static net.bluevine.chromagica.TestData.WHITE_FILAMENT_DATA;
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
import net.bluevine.chromagica.model.RGBColor;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class FilamentStackerTest {
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
    FilamentStacker stacker = new FilamentStacker(TEST_FILAMENT_DATA_MAP, "White", 4);

    FilamentStack result = stacker.optimizeColorSequence(new RGBColor(100, 150, 225));

    assertEquals(List.of("White", "Cyan", "Blue", "White"), result.getFilamentSequence());
  }

  @Test
  void optimizeColorSequence_topColor() {
    FilamentStacker stacker = new FilamentStacker(TEST_FILAMENT_DATA_MAP, "White", 4);

    FilamentStack result =
        stacker.optimizeColorSequence(new RGBColor(100, 150, 225), ImmutableList.of("Cyan"));

    assertEquals(List.of("White", "Blue", "White", "Cyan"), result.getFilamentSequence());
  }

  @Test
  void optimizeColorSequence_cacheException() throws Exception {
    try (MockedStatic<CacheBuilder> mockedCacheBuilder = mockStatic(CacheBuilder.class)) {
      Cache<List<String>, RGBColor> mockedCache = mock(Cache.class);
      CacheBuilder<List<String>, RGBColor> cacheBuilder = mock(CacheBuilder.class);
      when(cacheBuilder.maximumSize(anyLong())).thenReturn(cacheBuilder);
      when(cacheBuilder.build()).thenReturn(mockedCache);
      mockedCacheBuilder.when(CacheBuilder::newBuilder).thenReturn(cacheBuilder);

      when(mockedCache.get(anyList(), any()))
          .thenThrow(new ExecutionException(new IllegalArgumentException("Mocked exception")));

      // Manually test if the mock works as expected
      FilamentStacker stacker = new FilamentStacker(TEST_FILAMENT_DATA_MAP, "White", 4);

      RuntimeException thrown =
          assertThrows(
              RuntimeException.class,
              () -> stacker.optimizeColorSequence(new RGBColor(100, 150, 225)));
      assertInstanceOf(IllegalArgumentException.class, thrown.getCause());
    }
  }
}
