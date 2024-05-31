package com.mageddo.dnsproxyserver.config.dataprovider;

import com.mageddo.dnsproxyserver.config.CircuitBreaker;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.SolverRemote;
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
public class ConfigDAOJson implements ConfigDAO {

  private final ConfigDAOEnv configDAOEnv;
  private final ConfigDAOCmdArgs configDAOCmdArgs;

  @Override
  public Config find() {
    final var workDir = this.configDAOEnv.findRaw().getCurrentPath();
    final var relativeConfigFilePath = this.configDAOCmdArgs.findRaw().getConfigPath();
    final var configFileAbsolutePath = buildConfigPath(workDir, relativeConfigFilePath);
    return this.find(configFileAbsolutePath);
  }

  public Config find(Path configPath) {
    final var jsonConfig = JsonConfigs.loadConfig(configPath);
    log.debug("configPath={}", configPath);
    return toConfig(jsonConfig, configPath);
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
    return !Arrays.toString(ConfigDAOCmdArgs.getArgs()).contains("--conf-path") && Tests.inTest();
  }

  Config toConfig(ConfigJson json, Path configFileAbsolutePath) {
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
      .configPath(configFileAbsolutePath)
      .solverRemote(toSolverRemote(json))
      .build();
  }

  static SolverRemote toSolverRemote(ConfigJson json) {
    final var solverRemote = json.getSolverRemote();
    if (solverRemote == null) {
      return null;
    }
    final var circuitBreaker = solverRemote.getCircuitBreaker();
    if (circuitBreaker == null) {
      return null;
    }
    return SolverRemote
      .builder()
      .circuitBreaker(CircuitBreaker
        .builder()
        .failureThreshold(circuitBreaker.getFailureThreshold())
        .failureThresholdCapacity(circuitBreaker.getFailureThresholdCapacity())
        .successThreshold(circuitBreaker.getSuccessThreshold())
        .testDelay(circuitBreaker.getTestDelay())
        .build()
      )
      .build();
  }

  @Override
  public int priority() {
    return 2;
  }
}
