package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.dnsproxyserver.config.entrypoint.ConfigEnv;
import com.mageddo.dnsproxyserver.dnsconfigurator.linux.ResolvFile.Type;
import com.mageddo.dnsproxyserver.templates.IpTemplates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static com.mageddo.utils.Files.createIfNotExists;
import static com.mageddo.utils.Files.getPathName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class LinuxDnsConfiguratorTest {

  LinuxDnsConfigurator configurator = spy(new LinuxDnsConfigurator());

  @Test
  void mustConfigureDpsServerOnEmptyFileAsResolvconf(@TempDir Path tmpDir) throws Exception {

    // arrrange
    final var resolvFile = Files.createTempFile(tmpDir, "resolv", ".conf");
    final var ip = IpTemplates.local();

    doReturn(Collections.singletonList(resolvFile))
      .when(this.configurator)
      .buildConfPaths()
    ;

    // act
    this.configurator.configure(ip);

    // assert
    assertEquals(
      """
        nameserver 10.10.0.1 # dps-entry
        """,
      Files.readString(resolvFile)
    );

  }

  @Test
  void mustConfigureDpsServerOnEmptyFileAsResolved(@TempDir Path tmpDir) throws Exception {

    // arrrange
    final var resolvFile = Files.createFile(tmpDir.resolve("resolved.conf"));
    final var ip = IpTemplates.local();

    doReturn(Collections.singletonList(resolvFile))
      .when(this.configurator)
      .buildConfPaths()
    ;

    // act
    this.configurator.configure(ip);

    // assert
    assertEquals(
      """
        DNS=10.10.0.1 # dps-entry
        """,
      Files.readString(resolvFile)
    );

  }

  @Test
  void shouldntConfigureResolvedTwice(@TempDir Path tmpDir) throws Exception {

    // arrrange
    final var resolvFile = Files.createFile(tmpDir.resolve("resolved.conf"));
    final var ip = IpTemplates.local();

    doReturn(Collections.singletonList(resolvFile))
      .when(this.configurator)
      .buildConfPaths()
    ;

    // act
    this.configurator.configure(ip);
    this.configurator.configure(IpTemplates.loopback());

    // assert
    assertEquals(
      """
        DNS=10.10.0.1 # dps-entry
        """,
      Files.readString(resolvFile)
    );

  }

  @Test
  void mustSplitResolvPathConfigToMultiplePaths() {
    // arrange
    doReturn(ConfigEnv.DEFAULT_RESOLV_CONF_PATH)
      .when(this.configurator)
      .getConfigResolvPaths()
    ;

    // act
    final var paths = this.configurator.buildConfPaths();

    // assert
    assertEquals(
      "[/host/etc/systemd/resolved.conf, /host/etc/resolv.conf, /etc/systemd/resolved.conf, /etc/resolv.conf]",
      paths.toString()
    );
  }

  @Test
  void mustDetectTwoConfigFilesButUseTheSecondBecauseTheFirstIsNotOK(@TempDir Path tmpDir) {
    // arrange
    final var confA = tmpDir.resolve("resolved.conf");
    final var confB = createIfNotExists(tmpDir.resolve("resolv.conf"));

    doReturn(confA + "," + confB)
      .when(this.configurator)
      .getConfigResolvPaths()
    ;

    // act
    final var conf = this.configurator.findBestConfFile();

    // assert
    assertEquals(Type.RESOLVCONF, conf.getType());
    assertEquals("resolv.conf", getPathName(conf.getPath()));
  }
}
