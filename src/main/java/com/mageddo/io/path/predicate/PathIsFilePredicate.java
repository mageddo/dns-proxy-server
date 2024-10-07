package com.mageddo.io.path.predicate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

public class PathIsFilePredicate implements Predicate<Path> {
  @Override
  public boolean test(Path path) {
    return !Files.isDirectory(path);
  }
}
