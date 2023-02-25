package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.dnsproxyserver.server.dns.IP;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResolvconfConfiguratorTest {

  @Test
  void mustConfigureDpsServerOnEmptyFile(@TempDir Path tmpDir) throws Exception {

    // arrrange
    final var resolvFile = Files.createTempFile(tmpDir, "resolv", ".conf");

    // act
    ResolvconfConfigurator.process(resolvFile, IP.of("10.10.0.1"));

    // assert
    assertEquals(
      """
        nameserver 10.10.0.1 # dps-entry
        """,
      Files.readString(resolvFile)
    );

  }

  @Test
  void mustCommentExistingServerAndSetupPassedConf(@TempDir Path tmpDir) throws Exception {

    // arrrange
    final var resolvFile = tmpDir.resolve("resolv.conf");
    final var ip = IP.of("10.10.0.1");
    Files.writeString(resolvFile, "nameserver 8.8.8.8");

    // act
    ResolvconfConfigurator.process(resolvFile, ip);

    // assert
    assertEquals(
      """
        # nameserver 8.8.8.8 # dps-comment
        nameserver 10.10.0.1 # dps-entry
        """,
      Files.readString(resolvFile)
    );

  }


  @Test
  void mustUseAlreadyExistentDpsServerLine(@TempDir Path tmpDir) throws Exception {

    // arrrange
    final var resolvFile = tmpDir.resolve("resolv.conf");
    final var ip = IP.of("10.10.0.1");
    Files.writeString(resolvFile, "nameserver 8.8.8.8\nnameserver 4.4.4.4 # dps-entry");

    // act
    ResolvconfConfigurator.process(resolvFile, ip);
    // assert
    assertEquals(
      """
        # nameserver 8.8.8.8 # dps-comment
        nameserver 10.10.0.1 # dps-entry
        """,
      Files.readString(resolvFile)
    );

  }

  @Test
  void mustRestoreOriginalResolvconf(@TempDir Path tmpDir) throws Exception {

    // arrrange
    final var resolvFile = tmpDir.resolve("resolv.conf");

    Files.writeString(resolvFile, """
      # Provided by test
      # nameserver 7.7.7.7
      # nameserver 8.8.8.8 # dps-comment
      nameserver 9.9.9.9 # dps-entry
      """);

    // act
    ResolvconfConfigurator.restore(resolvFile);

    // assert
    assertEquals(
      """
        # Provided by test
        # nameserver 7.7.7.7
        nameserver 8.8.8.8
        """,
      Files.readString(resolvFile)
    );

  }
}
