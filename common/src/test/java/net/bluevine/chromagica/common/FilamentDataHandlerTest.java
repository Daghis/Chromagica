package net.bluevine.chromagica.common;

import static net.bluevine.chromagica.common.TestData.TEST_FILAMENT_DATA;
import static net.bluevine.chromagica.common.TestData.TEST_FILAMENT_DATA_AS_JSON;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import net.bluevine.chromagica.common.model.FilamentData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FilamentDataHandlerTest {
  private Path tempDataFile;

  @BeforeEach
  public void setUp() throws IOException {
    tempDataFile = Files.createTempFile("FilamentDataHandlerTest", ".json");
  }

  @AfterEach
  public void tearDown() throws IOException {
    Files.deleteIfExists(tempDataFile);
  }

  @Test
  void writeToFile() throws IOException {
    FilamentDataHandler.writeToFile(TEST_FILAMENT_DATA, tempDataFile);

    String result = Files.readString(tempDataFile);
    assertEquals(TEST_FILAMENT_DATA_AS_JSON, result);
  }

  @Test
  void writeToFile_emptyData() throws IOException {
    FilamentDataHandler.writeToFile(Map.of(), tempDataFile);

    String result = Files.readString(tempDataFile);
    assertEquals("{}", result);
  }

  @Test
  void readFromFile() throws IOException {
    Files.writeString(tempDataFile, TEST_FILAMENT_DATA_AS_JSON);
    Map<String, FilamentData> result = FilamentDataHandler.readFromFile(tempDataFile);

    assertEquals(TEST_FILAMENT_DATA, result);
  }

  /**
   * This test ensures that when the JSON content is "null", the readFromFile method returns an
   * empty map, thereby covering the branch where filamentDataMap is null.
   */
  @Test
  void readFromFile_nullFilamentDataMap() throws IOException {
    // Write "null" to the JSON file to simulate a null FilamentDataMap
    Files.writeString(tempDataFile, "null");

    // Invoke the method under test
    Map<String, FilamentData> result = FilamentDataHandler.readFromFile(tempDataFile);

    // Assert that the returned map is empty
    assertNotNull(result, "The returned map should not be null");
    assertTrue(result.isEmpty(), "The returned map should be empty when filamentDataMap is null");
  }

  @Test
  void privateConstructor_shouldThrowUnsupportedOperationException() throws Exception {
    // Obtain the private constructor of FilamentDataHandler
    Constructor<FilamentDataHandler> constructor =
        FilamentDataHandler.class.getDeclaredConstructor();

    // Make the private constructor accessible
    constructor.setAccessible(true);

    // Attempt to instantiate FilamentDataHandler and expect an InvocationTargetException
    InvocationTargetException invocationException =
        assertThrows(
            InvocationTargetException.class,
            constructor::newInstance,
            "Expected InvocationTargetException when invoking private constructor");

    // Assert that the cause of the InvocationTargetException is UnsupportedOperationException
    Throwable cause = invocationException.getCause();
    assertInstanceOf(UnsupportedOperationException.class, cause);
  }
}
