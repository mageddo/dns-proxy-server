package com.mageddo.dnsproxyserver.dnsconfigurator.linux.systemdresolved;

import com.mageddo.dnsproxyserver.dnsconfigurator.linux.ResolvedConfigurator;
import com.mageddo.dnsproxyserver.templates.IpAddrTemplates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResolvedConfiguratorTest {

  @Test
  void mustConfigureDnsServerOnEmptyFile(@TempDir Path tmpDir) throws Exception {
    // arrange
    final var confFile = Files.createTempFile(tmpDir, "file", ".conf");
    final var localIp = IpAddrTemplates.local();

    // act
    ResolvedConfigurator.configure(confFile, localIp);

    // assert
    assertEquals("""
            DNS=10.10.0.1 # dps-entry
            """,
        Files.readString(confFile)
    );
  }

  @Test
  void mustConfigureDnsServerOnAlreadyExstingFile(@TempDir Path tmpDir) throws Exception {
    // arrange
    final var confFile = Files.writeString(tmpDir.resolve("file.conf"), """
        #  This file is part of systemd.
        #
        [Resolve]
        #DNS=
        #DNS=172.157.5.1
        DNS=127.0.0.1
        #DNS=192.168.0.128
        #FallbackDNS=
        #Domains=
        """);
    final var localIp = IpAddrTemplates.local();

    // act
    ResolvedConfigurator.configure(confFile, localIp);

    // assert
    assertEquals("""
        #  This file is part of systemd.
        #
        [Resolve]
        #DNS=
        #DNS=172.157.5.1
        # DNS=127.0.0.1 # dps-comment
        #DNS=192.168.0.128
        #FallbackDNS=
        #Domains=
        DNS=10.10.0.1 # dps-entry
        """, Files.readString(confFile));
  }


  @Test
  void mustChangeActiveDNS(@TempDir Path tmpDir) throws Exception {
    // arrange
    final var confFile = Files.writeString(tmpDir.resolve("file.conf"), """
        [Resolve]
        DNS=8.8.8.8
        FallbackDNS=
        Domains=
        """);
    final var localIp = IpAddrTemplates.local();

    // act
    ResolvedConfigurator.configure(confFile, localIp);

    // assert
    assertEquals("""
        [Resolve]
        # DNS=8.8.8.8 # dps-comment
        FallbackDNS=
        Domains=
        DNS=10.10.0.1 # dps-entry
        """, Files.readString(confFile));
  }

  @Test
  void mustReuseDPSDNSLine(@TempDir Path tmpDir) throws Exception {
    // arrange
    final var confFile = Files.writeString(tmpDir.resolve("file.conf"), """
        [Resolve]
        DNS=192.168.0.1 # dps-entry
        """);
    final var localIp = IpAddrTemplates.local();

    // act
    ResolvedConfigurator.configure(confFile, localIp);

    // assert
    assertEquals("""
            [Resolve]
            DNS=10.10.0.1 # dps-entry
            """,
        Files.readString(confFile)
    );
  }

  @Test
  void mustRestore(@TempDir Path tmpDir) throws Exception {
    // arrange
    final var confFile = Files.writeString(
        tmpDir.resolve("file.conf"), """
            [Resolve]
            # DNS=8.8.8.8 # dps-comment
            FallbackDNS=
            Domains=
            DNS=10.10.0.1 # dps-entry
            """
    );

    // act
    ResolvedConfigurator.restore(confFile);

    // assert
    assertEquals("""
            [Resolve]
            DNS=8.8.8.8
            FallbackDNS=
            Domains=
            """,
        Files.readString(confFile)
    );
  }


  @Test
  void mustConfigureCustomPort(@TempDir Path tmpDir) throws Exception {
    // arrange
    final var confFile = Files.writeString(tmpDir.resolve("file.conf"), """
        [Resolve]
        """);
    final var addr = IpAddrTemplates.localPort54();

    // act
    ResolvedConfigurator.configure(confFile, addr);

    // assert
    assertEquals("""
            [Resolve]
            DNS=10.10.0.1:54 # dps-entry
            """,
        Files.readString(confFile)
    );
  }
}
