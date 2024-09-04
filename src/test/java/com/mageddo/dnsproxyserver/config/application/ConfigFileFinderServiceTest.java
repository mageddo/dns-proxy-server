package com.mageddo.dnsproxyserver.config.application;

import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAOCmdArgs;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAOEnv;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.ConfigFlagTemplates;
import testing.templates.config.ConfigEnvTemplates;

import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ConfigFileFinderServiceTest {

  @Mock
  ConfigDAOEnv configDAOEnv;

  @Mock
  ConfigDAOCmdArgs configDAOCmdArgs;

  @Spy
  @InjectMocks
  ConfigFileFinderService service;

  @Test
  void mustUseEnvConfig() {
    // arrange
    doReturn(ConfigEnvTemplates.withConfigFilePath())
      .when(this.configDAOEnv)
      .findRaw()
    ;

    doReturn(ConfigFlagTemplates.withConfigFilePath())
      .when(this.configDAOCmdArgs)
      .findRaw()
    ;

    // act
    final var path = this.service.findPath();

    // assert
    assertTrue(path.endsWith(Paths.get("some-place/config.json")), path.toString());

  }

  @Test
  void mustUseArgsConfigWhenEnvNotSet() {
    // arrange
    doReturn(ConfigEnvTemplates.empty())
      .when(this.configDAOEnv)
      .findRaw()
    ;

    doReturn(ConfigFlagTemplates.withConfigFilePath())
      .when(this.configDAOCmdArgs)
      .findRaw()
    ;

    // act
    final var path = this.service.findPath();

    // assert
    assertTrue(path.endsWith(Paths.get("flag-relative-path/flag-config.json")), path.toString());

  }


  @Test
  void mustUseRandomGeneratedConfigPathWhenRunningInTestsAndNoCustomPathIsSpecified() {
    // arrange
    doReturn(ConfigEnvTemplates.empty())
      .when(this.configDAOEnv)
      .findRaw()
    ;

    doReturn(ConfigFlagTemplates.empty())
      .when(this.configDAOCmdArgs)
      .findRaw()
    ;

    // act
    final var path = this.service.findPath();

    // assert
    assertThat(path.toString(), CoreMatchers.containsString("dns-proxy-server-junit"));

  }
}
