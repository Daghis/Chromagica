package net.bluevine.chromagica.imageto3dprint;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class StlGenerator {

  /**
   * Generates an STL file at the specified path from the provided SCAD content using OpenSCAD.
   *
   * @param scadContent The OpenSCAD content to be rendered as an STL.
   * @param stlPath The Path where the STL file should be saved.
   * @throws IOException If an error occurs during file I/O or OpenSCAD execution.
   * @throws InterruptedException If the OpenSCAD process is interrupted.
   */
  public static void createStlFile(String scadContent, Path stlPath)
      throws IOException, InterruptedException {
    // Create a temporary SCAD file
    File scadFile = File.createTempFile("model", ".scad");

    // Write the SCAD content to the temporary SCAD file
    try (FileWriter writer = new FileWriter(scadFile)) {
      writer.write(scadContent);
    }

    // Execute OpenSCAD to convert SCAD to STL
    ProcessBuilder processBuilder =
        new ProcessBuilder(
                "openscad",
                "--enable=manifold",
                "-o",
                stlPath.toAbsolutePath().toString(),
                scadFile.getAbsolutePath())
            .redirectOutput(new File("/dev/null"))
            .redirectError(new File("/dev/null"));
    Process process = processBuilder.start();

    // Wait for the OpenSCAD process to complete
    int exitCode = process.waitFor();
    if (exitCode != 0) {
      throw new IOException("OpenSCAD process failed with exit code: " + exitCode);
    }

    System.out.println("STL file created at: " + stlPath.toAbsolutePath());

    // Optionally delete the temporary SCAD file
    scadFile.deleteOnExit();
  }
}
