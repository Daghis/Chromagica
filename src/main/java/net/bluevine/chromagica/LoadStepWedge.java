package net.bluevine.chromagica;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  public static void main(String[] args) {
    int exitCode = new CommandLine(new LoadStepWedge()).execute(args);
    System.exit(exitCode);
  }

  private void validateArguments() {
    int numFilamentNames = filamentNames.size();
    if (numFilamentNames != numberOfColors) {
      String quantity =
          numFilamentNames == 1 ? "1 was" : String.format("%d were", numFilamentNames);
      throw new ParameterException(
          spec.commandLine(),
          String.format(
              "You must specify %d filament names; %s provided.", numberOfColors, quantity));
    }
  }

  @Override
  public void run() {
    validateArguments();

    // Process the image file
    System.out.println("Processing image file: " + imagePath);
    System.out.println("Number of colors: " + numberOfColors);
    System.out.println("Colors: " + filamentNames);

    // Add your processing logic here
    Map<String, FilamentData> filamentData;
    try {
      filamentData = FilamentDataHandler.readFromFile(databasePath);
    } catch (NoSuchFileException e) {
      filamentData = new HashMap<>();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    ColorAnalyzer analyzer = new ColorAnalyzer(filamentData);
    try {
      analyzer.analyze(imagePath.toString(), filamentNames, 16, 16);
      FilamentDataHandler.writeToFile(analyzer.filamentData, databasePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Example output
    System.out.println("Processing complete.");
  }
}
