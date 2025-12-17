package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import testing.templates.IpAddrTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResolvconfConfiguratorV2Test {

  @Test
  void mustConfigureDpsServerOnEmptyFile(@TempDir Path tmpDir) throws Exception {

    // arrrange
    final var resolvFile = Files.createTempFile(tmpDir, "resolv", ".conf");

    // act
    ResolvconfConfiguratorV2.process(resolvFile, IpAddrTemplates.local());

    // assert
    assertEquals("""
            # BEGIN dps-entries
            nameserver 10.10.0.1
            # END dps-entries
            """,
        Files.readString(resolvFile)
    );

  }


  @Test
  void mustCleanUpDpsCommentsAndEntriesBeforeApply(@TempDir Path tmpDir) throws Exception {

    // arrrange
    final var resolvFile = tmpDir.resolve("resolv.conf");
    final var ip = IpAddrTemplates.local();
    Files.writeString(resolvFile, """
            # BEGIN dps-entries
            nameserver 10.10.0.6
            # END dps-entries

            # BEGIN dps-comments
            # nameserver 5.5.5.5
            # END dps-comments

            nameserver 5.5.5.5 # dps-entry
            # nameserver 8.8.8.8 # dps-comment
            # nameserver 8.8.4.4 # dps-comment

            nameserver 8.8.8.8
        """);

    // act
    ResolvconfConfiguratorV2.process(resolvFile, ip);

    // assert
    assertEquals(
        """
            # BEGIN dps-entries
            nameserver 10.10.0.1
            # END dps-entries

            # BEGIN dps-comments
            # nameserver 8.8.8.8
            # nameserver 8.8.4.4
            # END dps-comments
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
    ResolvconfConfiguratorV2.process(resolvFile, ip);

    // assert
    assertEquals(
        """
            # BEGIN dps-entries
            nameserver 10.10.0.1
            # END dps-entries

            # BEGIN dps-comments
            # nameserver 8.8.8.8
            # END dps-comments
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
    ResolvconfConfiguratorV2.process(resolvFile, ip);
    // assert
    assertEquals(
        """
            # BEGIN dps-entries
            nameserver 10.10.0.1
            # END dps-entries

            # BEGIN dps-comments
            # nameserver 8.8.8.8
            # END dps-comments
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

        # BEGIN dps-entries
        nameserver 10.10.0.1
        # END dps-entries

        # BEGIN dps-comments
        # nameserver 8.8.4.4
        # END dps-comments
        """
    );

    // act
    ResolvconfConfiguratorV2.restore(resolvFile);

    // assert
    assertEquals(
        """
            # Provided by test
            # nameserver 7.7.7.7
            nameserver 8.8.8.8
            nameserver 8.8.4.4
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
          ResolvconfConfiguratorV2.process(resolvFile, addr);
        }
    );

    // assert
    final var msg = ex.getMessage();
    assertTrue(msg.contains("requires dns server port to"), msg);

  }

  @Test
  void mustNotCommentFollowingNameServersWhenNameserversOverrideIsDisabled(@TempDir Path tmpDir)
      throws Exception {

    // arrange
    final var resolvFile = tmpDir.resolve("resolv.conf");
    final var ip = IpAddrTemplates.local();

    Files.writeString(resolvFile, """
        # Provided by test
        nameserver 7.7.7.7
        # nameserver 8.8.8.8
        nameserver 8.8.4.4
        """
    );

    // act
    ResolvconfConfiguratorV2.process(resolvFile, ip, false);

    // assert
    assertEquals(
        """
            # Provided by test

            # BEGIN dps-entries
            nameserver 10.10.0.1
            # END dps-entries

            nameserver 7.7.7.7
            # nameserver 8.8.8.8
            nameserver 8.8.4.4
            """,
        Files.readString(resolvFile)
    );
  }


  @Test
  void mustCreateExactlyOneDpsEntryEvenWhenCalledTwice(@TempDir Path tmpDir)
      throws Exception {

    // arrange
    final var resolvFile = tmpDir.resolve("resolv.conf");
    final var ip = IpAddrTemplates.local();

    Files.writeString(resolvFile, """
        # Provided by test
        nameserver 7.7.7.7
        # nameserver 8.8.8.8
        nameserver 8.8.4.4
        """
    );

    // act
    ResolvconfConfiguratorV2.process(resolvFile, ip, false);
    ResolvconfConfiguratorV2.process(resolvFile, ip, false);

    // assert
    assertEquals(
        """
            # Provided by test

            # BEGIN dps-entries
            nameserver 10.10.0.1
            # END dps-entries

            nameserver 7.7.7.7
            # nameserver 8.8.8.8
            nameserver 8.8.4.4
            """,
        Files.readString(resolvFile)
    );
  }
}
