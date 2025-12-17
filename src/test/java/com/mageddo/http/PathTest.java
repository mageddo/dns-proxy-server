package com.mageddo.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PathTest {

  @Test
  void mustBuildPathFromSubdirs() {
    // arrange

    // ct
    final var path = Path.of("/", "do", "/stuff");

    // assert
    assertEquals("/do/stuff", path.getRaw());
  }

  @Test
  void mustBuildPathFromOnlyRootDir() {
    // arrange

    // ct
    final var path = Path.of("/");

    // assert
    assertEquals("/", path.getRaw());
  }

  @Test
  void pathWithFile() {
    // arrange

    // ct
    final var path = Path.of("/tmp", "style.css");

    // assert
    assertEquals("/tmp/style.css", path.getRaw());
  }
}
