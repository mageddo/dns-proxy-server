package com.mageddo.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class Files {
  public static Path createTempFileExitOnExit(final String prefix, final String suffix) {
    try {
      final var p = java.nio.file.Files.createTempFile(prefix, suffix);
      p.toFile().deleteOnExit();
      return p;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static boolean exists(Path p) {
    return java.nio.file.Files.exists(p);
  }
}
