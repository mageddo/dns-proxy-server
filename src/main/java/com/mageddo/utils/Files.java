package com.mageddo.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class Files {
  public static Path createTempFileDeleteOnExit(final String prefix, final String suffix) {
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

  public static Path createIfNotExists(Path path) {
    if (Files.exists(path)) {
      return path;
    }
    try {
      java.nio.file.Files.createFile(path);
      return path;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static String getPathName(Path path) {
    return path.getFileName().toString();
  }
}
