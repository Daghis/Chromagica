package net.bluevine.chromagica.loadstepwedge;

import static net.bluevine.chromagica.loadstepwedge.TestData.FILAMENT_DATABASE_PATH;
import static net.bluevine.chromagica.loadstepwedge.TestData.FILAMENT_NAMES;
import static net.bluevine.chromagica.loadstepwedge.TestData.ORANGE_1x1_IMAGE_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import net.bluevine.chromagica.common.model.FilamentData;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import picocli.CommandLine;
import picocli.CommandLine.ExitCode;

public class LoadStepWedgeTest {
  private static final int MAX_COLORS_ALLOWED = 16;

  private ByteArrayOutputStream err;
  CommandLine command;

  @BeforeEach
  public void setUp() {
    err = new ByteArrayOutputStream();

    command = new CommandLine(new LoadStepWedge());
    command.setErr(new PrintWriter(err));
  }

  @Test
  public void numberOfColors_mustNotBeZero() {
    int status = command.execute("-n", "0", ORANGE_1x1_IMAGE_PATH);

    assertEquals(ExitCode.USAGE, status);
    assertThat(err.toString(), containsString("between 1 and 16"));
  }

  @Test
  public void numberOfColors_mustNotBeTooLarge() {
    int numColorsTried = MAX_COLORS_ALLOWED + 1;
    int status =
        command.execute(
            "-n",
            String.valueOf(numColorsTried),
            ORANGE_1x1_IMAGE_PATH,
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15",
            "16",
            "17");

    assertEquals(ExitCode.USAGE, status);
    assertThat(err.toString(), containsString("between 1 and 16"));
  }

  @Test
  public void validateArguments_fails_single() {
    assertEquals(ExitCode.USAGE, command.execute("-n", "2", "unused.png", "OneColorName"));
    assertThat(err.toString(), containsString("2 filament names; 1 was provided"));
  }

  @Test
  public void validateArguments_fails_plural() {
    assertEquals(ExitCode.USAGE, command.execute("-n", "1", "unused.png", "One", "Two"));
    assertThat(err.toString(), containsString("1 filament name; 2 were provided"));
  }

  @Test
  public void run_fileNotFound() {
    assertEquals(ExitCode.SOFTWARE, command.execute("-n", "1", "file-not-found.png", "One"));
    assertThat(err.toString(), containsString("Can't read input file"));
  }

  @Test
  public void loadFilamentData() {
    Map<String, FilamentData> filamentData = LoadStepWedge.loadFilamentData(FILAMENT_DATABASE_PATH);
    assertThat(filamentData.entrySet(), hasSize(FILAMENT_NAMES.size()));
  }

  @Test
  public void loadFilamentData_doesNotExist() throws IOException {
    Path doesNotExist = Files.createTempFile("does-not-exist", ".json");
    Files.deleteIfExists(doesNotExist);

    assertTrue(Files.notExists(doesNotExist));
    Map<String, FilamentData> filamentData = LoadStepWedge.loadFilamentData(doesNotExist);
    assertThat(filamentData.entrySet(), is(empty()));
  }

  @Test
  public void loadFilamentData_otherException() {
    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class, CALLS_REAL_METHODS)) {
      mockedFiles
          .when(() -> Files.readString(any()))
          .thenThrow(new IOException("An IO error occurred."));

      Throwable exception =
          assertThrows(
              RuntimeException.class, () -> LoadStepWedge.loadFilamentData(FILAMENT_DATABASE_PATH));
      assertInstanceOf(IOException.class, exception.getCause());
    }
  }

  @Test
  public void run_processSimpleImage() throws IOException {
    String randomName = RandomStringUtils.randomAlphanumeric(10);
    Path tempFilamentDatabase = Files.createTempFile("test_filament_database", ".json");
    Files.deleteIfExists(tempFilamentDatabase);

    int status =
        command.execute(
            "-d", tempFilamentDatabase.toString(), "-n", "1", ORANGE_1x1_IMAGE_PATH, randomName);
    assertEquals(ExitCode.OK, status);

    String databaseResult = Files.readString(tempFilamentDatabase);
    assertThat(databaseResult, containsString(String.format("\"%s\": {", randomName)));

    // Cleanup
    tempFilamentDatabase.toFile().deleteOnExit();
  }

  private static void dummyExitFunction(int status) {
    // Nothing to do
  }

  @Test
  public void testMain() throws IOException {
    Path tempFilamentDatabase = Files.createTempFile("test_filament_database", ".json");
    Files.deleteIfExists(tempFilamentDatabase);

    LoadStepWedge.setExitFunction(LoadStepWedgeTest::dummyExitFunction);
    LoadStepWedge.main(
        new String[] {
          "-d", tempFilamentDatabase.toString(), "-n", "1", ORANGE_1x1_IMAGE_PATH, "Orange"
        });
    String databaseResult = Files.readString(tempFilamentDatabase);
    assertThat(databaseResult, containsString("\"Orange\": {"));

    // Cleanup
    tempFilamentDatabase.toFile().deleteOnExit();
  }
}
