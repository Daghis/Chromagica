package net.bluevine.chromagica;

import static net.bluevine.chromagica.TestData.TEST_FILAMENT_DATA;
import static net.bluevine.chromagica.TestData.TEST_FILAMENT_DATA_AS_JSON;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import net.bluevine.chromagica.model.FilamentData;
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

  @Test
  void colorUtil_constructorIsPrivate() throws Exception {
    Constructor<FilamentDataHandler> constructor =
        FilamentDataHandler.class.getDeclaredConstructor();
    assertFalse(constructor.canAccess(null));
  }

  @Test
  void colorUtil_constructorException() throws Exception {
    Constructor<FilamentDataHandler> constructor =
        FilamentDataHandler.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
    assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
  }
}
