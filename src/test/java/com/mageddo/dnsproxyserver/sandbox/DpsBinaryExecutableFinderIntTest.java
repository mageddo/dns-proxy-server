package com.mageddo.dnsproxyserver.sandbox;

import org.graalvm.nativeimage.ImageInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class DpsBinaryExecutableFinderIntTest {

  @Test
  void mustFindDpsNativeExecutablePath() {
    assumeTrue(ImageInfo.inImageRuntimeCode());

    final var found = DpsBinaryExecutableFinder.findPath();

    assertTrue(found.toString()
        .endsWith("-tests"));
  }

  @Test
  void mustFindDpsJarPath() {
    assumeFalse(ImageInfo.inImageRuntimeCode());

    final var found = DpsBinaryExecutableFinder.findPath();

    assertTrue(found.toString()
        .endsWith(".jar"));
  }


}
