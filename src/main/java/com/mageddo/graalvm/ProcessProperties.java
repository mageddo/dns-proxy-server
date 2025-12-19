package com.mageddo.graalvm;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.graalvm.nativeimage.ProcessProperties.getExecutableName;

public class ProcessProperties {
  public static Path getRunningPath() {
    return Paths.get(getExecutableName())
        .getParent();
  }
}
