package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.utils.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinuxResolverConfDetectorTest {
  @Test
  void mustDetectEmptyFileAsResolvConf(@TempDir Path tmpDir) throws Exception {
    // arrange
    final var conf = Files.createIfNotExists(tmpDir.resolve("resolv.conf"));

    // act
    final var type = LinuxResolverConfDetector.build(conf);

    // assert
    assertEquals(ResolvFile.Type.RESOLVCONF, type);
  }

  @Test
  void mustDetectEmptyFileAsResolved(@TempDir Path tmpDir) throws Exception {
    // arrange
    final var conf = Files.createIfNotExists(tmpDir.resolve("resolved.conf"));

    // act
    final var type = LinuxResolverConfDetector.build(conf);

    // assert
    assertEquals(ResolvFile.Type.SYSTEMD_RESOLVED, type);
  }
}
