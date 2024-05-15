package com.mageddo.dnsproxyserver.config.dataprovider;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.mapper.ConfigFieldsValuesMapper;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJson;
import com.mageddo.utils.Files;
import com.mageddo.utils.Runtime;
import com.mageddo.utils.Tests;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class MultiSourceConfigDAOJson implements MultiSourceConfigDAO {

  private final MultiSourceConfigDAOEnv configDAOEnv;
  private final MultiSourceConfigDAOCmdArgs configDAOCmdArgs;

  @Override
  public Config find() {
    final var configPath = buildConfigPath(
      this.configDAOEnv.findRaw().getCurrentPath(),
      this.configDAOCmdArgs.findRaw().getConfigPath()
    );
    final var jsonConfig = JsonConfigs.loadConfig(configPath);
    log.info("status=configuring, configFile={}", configPath);
    return toConfig(jsonConfig);
  }

  public static Path buildConfigPath(Path workDir, String configPath) {
    if (runningInTestsAndNoCustomConfigPath()) {
      return Files.createTempFileDeleteOnExit("dns-proxy-server-junit", ".json");
    }
    if (workDir != null) {
      return workDir
        .resolve(configPath)
        .toAbsolutePath()
        ;
    }
    final var confRelativeToCurrDir = Paths
      .get(configPath)
      .toAbsolutePath();
    if (Files.exists(confRelativeToCurrDir)) {
      return confRelativeToCurrDir;
    }
    return Runtime.getRunningDir()
      .resolve(configPath)
      .toAbsolutePath();
  }

  static boolean runningInTestsAndNoCustomConfigPath() {
    return !Arrays.toString(MultiSourceConfigDAOCmdArgs.getArgs()).contains("--conf-path") && Tests.inTest();
  }

  Config toConfig(ConfigJson json) {
    return Config.builder()
      .webServerPort(json.getWebServerPort())
      .dnsServerPort(json.getDnsServerPort())
      .defaultDns(json.getDefaultDns())
      .logLevel(ConfigFieldsValuesMapper.mapLogLevelFrom(json.getLogLevel()))
      .logFile(ConfigFieldsValuesMapper.mapLogFileFrom(json.getLogFile()))
      .registerContainerNames(json.getRegisterContainerNames())
      .hostMachineHostname(json.getHostMachineHostname())
      .domain(json.getDomain())
      .mustConfigureDpsNetwork(json.getDpsNetwork())
      .dpsNetworkAutoConnect(json.getDpsNetworkAutoConnect())
      .remoteDnsServers(json.getRemoteDnsServers())
      .serverProtocol(json.getServerProtocol())
      .dockerHost(json.getDockerHost())
      .resolvConfOverrideNameServers(json.getResolvConfOverrideNameServers())
      .noRemoteServers(json.getNoRemoteServers())
      .noEntriesResponseCode(json.getNoEntriesResponseCode())
      .dockerSolverHostMachineFallbackActive(json.getDockerSolverHostMachineFallbackActive())
      .build();
  }

  @Override
  public int priority() {
    return 2;
  }
}
