package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import java.nio.file.Path;

import com.mageddo.dnsproxyserver.dnsconfigurator.linux.ResolvFile.Type;
import com.mageddo.utils.Files;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static java.nio.file.Files.writeString;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LinuxResolverConfDetectorTest {
  @Test
  void mustDetectEmptyFileAsResolvConf(@TempDir Path tmpDir) throws Exception {
    // arrange
    final var conf = Files.createIfNotExists(tmpDir.resolve("resolv.conf"));

    // act
    final var type = LinuxResolverConfDetector.detect(conf);

    // assert
    assertEquals(Type.RESOLVCONF, type);
  }

  @Test
  void mustDetectEmptyFileAsResolved(@TempDir Path tmpDir) throws Exception {
    // arrange
    final var conf = Files.createIfNotExists(tmpDir.resolve("resolved.conf"));

    // act
    final var type = LinuxResolverConfDetector.detect(conf);

    // assert
    assertEquals(Type.SYSTEMD_RESOLVED, type);
  }

  @Test
  void mustDetectResolvedFile(@TempDir Path tmpDir) throws Exception {
    // arrange
    final var conf = tmpDir.resolve("xpto.conf");
    writeString(
        conf,
        """
            [Resolve]
            # Some examples of DNS servers which may be used for DNS= and FallbackDNS=:
            # Cloudflare: 1.1.1.1
            # Google:     8.8.8.8
            # Quad9:      9.9.9.9
            DNS=
            """
    );
    // act
    final var type = LinuxResolverConfDetector.detect(conf);

    // assert
    assertEquals(Type.SYSTEMD_RESOLVED, type);
  }

  @Test
  void mustDetectResolvConfFile(@TempDir Path tmpDir) throws Exception {
    // arrange
    final var conf = tmpDir.resolve("xpto.conf");
    writeString(
        conf,
        """
            nameserver 0.0.0.0
            """
    );
    // act
    final var type = LinuxResolverConfDetector.detect(conf);

    // assert
    assertEquals(Type.RESOLVCONF, type);
  }
}
