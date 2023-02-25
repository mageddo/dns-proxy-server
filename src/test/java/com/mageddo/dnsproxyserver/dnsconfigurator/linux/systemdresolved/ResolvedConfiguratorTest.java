package com.mageddo.dnsproxyserver.dnsconfigurator.linux.systemdresolved;

import com.mageddo.dnsproxyserver.templates.IpTemplates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResolvedConfiguratorTest {

  @Test
  void mustConfigureDnsServerOnEmptyFile(@TempDir Path tmpDir) throws Exception {
    // arrange
    final var confFile = Files.createTempFile(tmpDir, "f", ".conf");
    final var localIp = IpTemplates.local();

    // act
    ResolvedConfigurator.configure(confFile, localIp);

    // assert
    assertEquals(
      """
      DNS=10.10.0.1 # dps-entry
      """,
      Files.readString(confFile)
    );
  }

}
