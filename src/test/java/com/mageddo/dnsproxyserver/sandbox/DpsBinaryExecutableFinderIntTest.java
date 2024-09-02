package com.mageddo.dnsproxyserver.sandbox;

import org.graalvm.nativeimage.ImageInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class DpsBinaryExecutableFinderIntTest {

  @Test
  void mustFindDpsNativeExecutablePath(){
    assumeTrue(ImageInfo.inImageRuntimeCode());

    final var found = DpsBinaryExecutableFinder.find();

    assertTrue(found.toString().endsWith("-tests"));
  }

  @Test
  void mustFindDpsJarPath(){
    assertFalse(ImageInfo.inImageRuntimeCode());

    final var found = DpsBinaryExecutableFinder.find();

    assertTrue(found.toString().endsWith(".jar"));
  }


}
