package net.bluevine.chromagica;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.bluevine.chromagica.model.FilamentData;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Command(
    name = "LoadStepWedge",
    mixinStandardHelpOptions = true,
    version = "1.0",
    description = "Processes an image file of the step wedge print.")
public class LoadStepWedge implements Runnable {
  @Spec CommandSpec spec;

  @Parameters(index = "0", description = "The image file for the step wedge print.")
  private Path imagePath;

  @Parameters(
      index = "1..*",
      arity = "1..*",
      description = "Filament names used in the step wedge print, in order")
  private List<String> filamentNames;

  private int numberOfColors;

  @Option(
      names = {"-n", "--num-colors"},
      description = "Number of colors (1-16).",
      required = true)
  private void setNumberOfColors(int numberOfColors) {
    if (numberOfColors < 1 || numberOfColors > 16) {
      throw new ParameterException(
          spec.commandLine(), "Number of colors must be between 1 and 16.");
    }
    this.numberOfColors = numberOfColors;
  }

  @Option(
      names = {"-d", "--database"},
      description = "Path to database.",
      defaultValue = "filament_database.json")
  private Path databasePath;

  @Option(
      names = {"-c", "--chip-cols"},
      description = "Number of columns of chips per color block",
      defaultValue = "3")
  private int chipCols;

  @Option(
      names = {"-r", "--chip-rows"},
      description = "Number of rows of chips per color block",
      defaultValue = "3")
  private int chipRows;

  private static Consumer<Integer> exitFunction = System::exit;

  static void setExitFunction(Consumer<Integer> exitFunction) {
    LoadStepWedge.exitFunction = exitFunction;
  }

  void validateArguments() {
    int numFilamentNames = filamentNames.size();
    if (numFilamentNames != numberOfColors) {
      String quantity =
          numFilamentNames == 1 ? "1 was" : String.format("%d were", numFilamentNames);
      throw new ParameterException(
          spec.commandLine(),
          String.format(
              "You must specify %d filament name%s; %s provided.",
              numberOfColors, numberOfColors != 1 ? "s" : "", quantity));
    }
  }

  static Map<String, FilamentData> loadFilamentData(Path databasePath) {
    Map<String, FilamentData> filamentData;

    try {
      filamentData = FilamentDataHandler.readFromFile(databasePath);
    } catch (NoSuchFileException e) {
      filamentData = new HashMap<>();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return filamentData;
  }

  @Override
  public void run() {
    validateArguments();

    System.out.println("Processing image file: " + imagePath);
    System.out.println("Number of colors: " + numberOfColors);
    System.out.println("Colors: " + filamentNames);

    Map<String, FilamentData> filamentData = loadFilamentData(databasePath);

    ColorAnalyzer analyzer = new ColorAnalyzer(filamentData);
    try {
      analyzer.analyze(imagePath.toString(), filamentNames, chipCols, chipRows);
      FilamentDataHandler.writeToFile(analyzer.filamentData, databasePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Example output
    System.out.println("Processing complete.");
  }

  public static void main(String[] args) {
    int exitCode = new CommandLine(new LoadStepWedge()).execute(args);
    exitFunction.accept(exitCode);
  }
}
