package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.dnsproxyserver.dnsconfigurator.linux.LinuxDnsConfigurator;
import com.mageddo.dnsproxyserver.server.dns.IP;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinuxDnsConfiguratorTest {

  LinuxDnsConfigurator configurator = new LinuxDnsConfigurator();

  @Test
  void mustConfigureDpsServerOnEmptyFile(@TempDir Path tmpDir) throws Exception {

    // arrrange
    final var resolvFile = Files.createTempFile(tmpDir, "resolv", ".conf");

    // act
    this.configurator.configure(IP.of("10.10.0.1"), resolvFile);

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
    Files.writeString(resolvFile, "nameserver 8.8.8.8");

    // act
    this.configurator.configure(IP.of("10.10.0.1"), resolvFile);

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
    Files.writeString(resolvFile, "nameserver 8.8.8.8\nnameserver 4.4.4.4 # dps-entry");

    // act
    this.configurator.configure(IP.of("10.10.0.1"), resolvFile);

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
    this.configurator.restore(resolvFile);

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
