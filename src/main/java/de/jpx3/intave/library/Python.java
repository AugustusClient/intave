package de.jpx3.intave.library;

import de.jpx3.intave.IntaveLogger;

import java.io.*;
import java.nio.file.Files;

public class Python {
  private static boolean available = false;

  public static void setup() {
    available = isPythonInstalled();

    if (!available) {
      IntaveLogger.logger().error("Unable to find Python installation.");
      IntaveLogger.logger().error("For improved functionality, please install Python 3.8.5 or higher.");
      return;
    }

    String[] libraries = new String[] {
      "numpy", "scipy", "scikit-learn", "tensorflow", "keras", "pandas", "matplotlib", "seaborn", "statsmodels"
    };

    for (String library : libraries) {
      installLibrary(pythonByPath(), library);
    }
  }

  public static int runScript(InputStream script) {
    // copy to temp file
//    File tempFile = Files.createTempFile("intave", ".py").toFile();

    return 0;
  }

  public static boolean available() {
    return available;
  }

  private static void installLibrary(
    String path,
    String library
  ) {
    if (path == null) {
      throw new IllegalArgumentException("path cannot be null");
    }
    if (library == null) {
      throw new IllegalArgumentException("library cannot be null");
    }
    if (isLibraryInstalled(path, library)) {
      return;
    }
    IntaveLogger.logger().info("Installing Python library: " + library);
    exec(path + " -m pip install " + library);
  }

  private static boolean isLibraryInstalled(
    String path,
    String library
  ) {
    // show
    String output = execWithOutput(path + " -m pip show " + library);
    return output.contains("Name: " + library);
  }

  private static boolean isPythonInstalled() {
    return pythonByPath() != null;
  }

  private static String pythonByPath() {
    String[] commands = new String[]{"python3", "python", "py", "py3"};
    for (String command : commands) {
      if (exec(command + " --version") == 0) {
        return command;
      }
    }
    return null;
  }

  private static String execWithOutput(
    String command
  ) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    exec(command, stream);
    return stream.toString();
  }

  private static int exec(String command) {
    return exec(command, System.out);
  }

  private static int exec(
    String command,
    OutputStream out
  ) {
    System.out.println("executing command: " + command);
    try {
      Process process = Runtime.getRuntime().exec(command);
      InputStream inputStream = process.getInputStream();

      if (out != null) {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) > 0) {
          out.write(buffer, 0, read);
        }
        out.close();
      }
      return process.waitFor();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
      return -1;
    }
  }
}
