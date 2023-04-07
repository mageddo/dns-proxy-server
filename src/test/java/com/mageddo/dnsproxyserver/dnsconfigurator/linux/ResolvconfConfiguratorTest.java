package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import testing.templates.IpAddrTemplates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResolvconfConfiguratorTest {

  @Test
  void mustConfigureDpsServerOnEmptyFile(@TempDir Path tmpDir) throws Exception {

    // arrrange
    final var resolvFile = Files.createTempFile(tmpDir, "resolv", ".conf");

    // act
    ResolvconfConfigurator.process(resolvFile, IpAddrTemplates.local());

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
    final var ip = IpAddrTemplates.local();
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
    final var ip = IpAddrTemplates.local();
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

    // arrange
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

  @Test
  void wontConfigurePortsDifferentFrom53(@TempDir Path tmpDir) throws Exception {

    // arrrange
    final var addr = IpAddrTemplates.localPort54();
    final var resolvFile = tmpDir.resolve("resolv.conf");

    // act
    final var ex = assertThrows(IllegalArgumentException.class, () -> {
      ResolvconfConfigurator.process(resolvFile, addr);
    });

    // assert
    final var msg = ex.getMessage();
    assertTrue(msg.contains("requires dns server port to"), msg);

  }

  @Test
  void mustNotCommentFollowingNameServersWhenNameserversOverrideIsDisabled(@TempDir Path tmpDir) throws Exception {

    // arrange
    final var resolvFile = tmpDir.resolve("resolv.conf");
    final var ip = IpAddrTemplates.local();

    Files.writeString(resolvFile, """
      # Provided by test
      nameserver 7.7.7.7
      # nameserver 8.8.8.8
      nameserver 8.8.4.4
      """);

    // act
    ResolvconfConfigurator.process(resolvFile, ip, false);

    // assert
    assertEquals(
      """
        # Provided by test
        nameserver 10.10.0.1 # dps-entry
        nameserver 7.7.7.7
        # nameserver 8.8.8.8
        nameserver 8.8.4.4
        """,
      Files.readString(resolvFile)
    );
  }


  @Test
  void mustCreateExactlyOneDpsEntryWhenNameserversOverrideIsDisabled(@TempDir Path tmpDir) throws Exception {

    // arrange
    final var resolvFile = tmpDir.resolve("resolv.conf");
    final var ip = IpAddrTemplates.local();

    Files.writeString(resolvFile, """
      # Provided by test
      nameserver 7.7.7.7
      # nameserver 8.8.8.8
      nameserver 8.8.4.4
      """);

    // act
    ResolvconfConfigurator.process(resolvFile, ip, false);
    ResolvconfConfigurator.process(resolvFile, ip, false);

    // assert
    assertEquals(
      """
        # Provided by test
        nameserver 10.10.0.1 # dps-entry
        nameserver 7.7.7.7
        # nameserver 8.8.8.8
        nameserver 8.8.4.4
        """,
      Files.readString(resolvFile)
    );
  }
}
