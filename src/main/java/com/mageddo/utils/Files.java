package com.mageddo.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;

public class Files {
  public static Path createTempFileDeleteOnExit(final String prefix, final String suffix) {
    try {
      final var p = java.nio.file.Files.createTempFile(prefix, suffix);
      p.toFile()
          .deleteOnExit();
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
    return path.getFileName()
        .toString();
  }

  /**
   * Copy content but don't touch on the file permissions, java.nio.file.Files.copy() with
   * REPLACE_EXISTING will change
   * the file permissions.
   *
   * @param source
   * @param target
   */
  public static void copyContent(Path source, Path target) {
    try (var in = newInputStream(source); var out = newOutputStream(target)) {
      IOUtils.copy(in, out);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Path pathOf(String path) {
    if (path == null) {
      return null;
    }
    return Paths.get(path);
  }

  public static String findExtension(Path path) {
    return com.google.common.io.Files.getFileExtension(path.getFileName()
        .toString());
  }

  public static void deleteQuietly(Path path) {
    FileUtils.deleteQuietly(path.toFile());
  }
}
