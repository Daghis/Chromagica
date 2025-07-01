package net.bluevine.chromagica.imageto3dprint;

import static java.util.Collections.emptyMap;
import static net.bluevine.chromagica.imageto3dprint.TestData.TEST_FILAMENT_DATA_MAP;
import static net.bluevine.chromagica.imageto3dprint.TestData.WHITE_COLOR;
import static net.bluevine.chromagica.imageto3dprint.TestData.WHITE_FILAMENT_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import net.bluevine.chromagica.common.model.FilamentData;
import net.bluevine.chromagica.common.model.RGBColor;
import net.bluevine.chromagica.imageto3dprint.FilamentStacker.FilamentStack;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

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
    FilamentStacker stacker = new FilamentStacker(TEST_FILAMENT_DATA_MAP, "White", 4);
    Cache<List<String>, RGBColor> cache =
        CacheBuilder.newBuilder()
            .maximumSize(FilamentStacker.PERMUTATION_CACHE_SIZE)
            .build();
    Cache<List<String>, RGBColor> spyCache = Mockito.spy(cache);

    Mockito.doThrow(new ExecutionException(new IllegalArgumentException("Mocked exception")))
        .when(spyCache)
        .get(anyList(), any());

    stacker.setPermutationCache(spyCache);

    RuntimeException thrown =
        assertThrows(
            RuntimeException.class,
            () -> stacker.optimizeColorSequence(new RGBColor(100, 150, 225)));
    assertInstanceOf(IllegalArgumentException.class, thrown.getCause());
  }
}
