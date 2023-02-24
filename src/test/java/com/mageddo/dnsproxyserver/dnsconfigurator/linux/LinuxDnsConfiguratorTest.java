package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.dnsproxyserver.server.dns.IP;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class LinuxDnsConfiguratorTest {

  LinuxDnsConfigurator configurator = spy(new LinuxDnsConfigurator());

  @Test
  void mustConfigureDpsServerOnEmptyFile(@TempDir Path tmpDir) throws Exception {

    // arrrange
    final var resolvFile = Files.createTempFile(tmpDir, "resolv", ".conf");
    doReturn(resolvFile)
      .when(this.configurator)
      .getConfPath()
    ;

    // act
    this.configurator.configure(IP.of("10.10.0.1"));

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
    doReturn(resolvFile)
      .when(this.configurator)
      .getConfPath()
    ;

    // act
    this.configurator.configure(IP.of("10.10.0.1"));

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
    doReturn(resolvFile)
      .when(this.configurator)
      .getConfPath()
    ;

    // act
    this.configurator.configure(IP.of("10.10.0.1"));

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
    doReturn(resolvFile)
      .when(this.configurator)
      .getConfPath()
    ;

    // act
    this.configurator.restore();

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
