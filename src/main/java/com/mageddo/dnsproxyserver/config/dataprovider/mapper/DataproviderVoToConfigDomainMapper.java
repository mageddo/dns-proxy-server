package com.mageddo.dnsproxyserver.config.dataprovider.mapper;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.LogLevel;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigPropDAO;
import com.mageddo.dnsproxyserver.config.dataprovider.JsonConfigs;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigEnv;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigFlag;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJson;
import com.mageddo.dnsproxyserver.server.dns.SimpleServer;
import com.mageddo.dnsproxyserver.utils.Numbers;
import com.mageddo.net.IpAddr;
import com.mageddo.utils.Files;
import com.mageddo.utils.Runtime;
import com.mageddo.utils.Tests;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mageddo.dnsproxyserver.utils.ObjectUtils.firstNonBlankRequiring;
import static com.mageddo.dnsproxyserver.utils.ObjectUtils.firstNonNullRequiring;

@Slf4j
public class DataproviderVoToConfigDomainMapper {

  public static Config build(ConfigFlag flag, ConfigEnv env, ConfigJson json, Path configPath) {
    return Config.builder()
      .version(ConfigPropDAO.getVersion())
      .webServerPort(Numbers.positiveOrDefault(json.getWebServerPort(), flag.getWebServerPort()))
      .dnsServerPort(Numbers.positiveOrDefault(json.getDnsServerPort(), flag.getDnsServerPort()))
      .defaultDns(firstNonNullRequiring(json.getDefaultDns(), flag.getDefaultDns()))
      .logLevel(buildLogLevel(firstNonNullRequiring(env.getLogLevel(), json.getLogLevel(), flag.getLogLevel())))
      .logFile(parseLogFile(firstNonBlankRequiring(env.getLogFile(), json.getLogFile(), flag.getLogToFile())))
      .registerContainerNames(firstNonNullRequiring(
        env.getRegisterContainerNames(), json.getRegisterContainerNames(), flag.getRegisterContainerNames()
      ))
      .hostMachineHostname(firstNonBlankRequiring(
        env.getHostMachineHostname(), json.getHostMachineHostname(), flag.getHostMachineHostname()
      ))
      .domain(firstNonBlankRequiring(
        env.getDomain(), json.getDomain(), flag.getDomain()
      ))
      .mustConfigureDpsNetwork(firstNonNullRequiring(
        env.getDpsNetwork(), json.getDpsNetwork(), flag.getDpsNetwork()
      ))
      .dpsNetworkAutoConnect(firstNonNullRequiring(
        env.getDpsNetworkAutoConnect(), json.getDpsNetworkAutoConnect(), flag.getDpsNetworkAutoConnect()
      ))
      .remoteDnsServers(buildRemoteServers(json.getRemoteDnsServers()))
      .configPath(configPath)
      .resolvConfPaths(env.getResolvConfPath())
      .serverProtocol(firstNonNullRequiring(
        json.getServerProtocol(), SimpleServer.Protocol.UDP_TCP
      ))
      .dockerHost(ObjectUtils.firstNonNull(
        flag.getDockerHost(), env.getDockerHost(), json.getDockerHost(), buildDefaultDockerHost()
      ))
      .resolvConfOverrideNameServers(firstNonNullRequiring(
        env.getResolvConfOverrideNameServers(), json.getResolvConfOverrideNameServers(), flag.getResolvConfOverrideNameServers()
      ))
      .noRemoteServers(firstNonNullRequiring(
        env.getNoRemoteServers(), json.getNoRemoteServers(), flag.getNoRemoteServers()
      ))
      .noEntriesResponseCode(firstNonNullRequiring(
        env.getNoEntriesResponseCode(), json.getNoEntriesResponseCode(), flag.getNoEntriesResponseCode()
      ))
      .dockerSolverHostMachineFallbackActive(firstNonNullRequiring(
        env.getDockerSolverHostMachineFallbackActive(),
        json.getDockerSolverHostMachineFallbackActive(),
        flag.getDockerSolverHostMachineFallbackActive()
      ))
      .build();
  }

  private static URI buildDefaultDockerHost() {
    if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
      return URI.create("unix:///var/run/docker.sock");
    }
    if (SystemUtils.IS_OS_WINDOWS) {
      return URI.create("npipe:////./pipe/docker_engine");
    }
    return null; // todo unsupported OS
  }

  static List<IpAddr> buildRemoteServers(List<IpAddr> servers) {
    if (servers == null || servers.isEmpty()) {
      return Collections.singletonList(IpAddr.of("8.8.8.8:53"));
    }
    return servers;
  }

  static LogLevel buildLogLevel(String logLevelName) {
    final var level = EnumUtils.getEnumIgnoreCase(LogLevel.class, logLevelName);
    if (StringUtils.isNotBlank(logLevelName) && level == null) {
      log.warn("status=couldntParseLogLevel, action=changesWillTakeNoEffect, proposedValue={}", logLevelName);
    }
    return level;
  }

  public static String parseLogFile(String v) {
    return switch (StringUtils.lowerCase(v)) {
      case "true" -> "/var/log/dns-proxy-server.log";
      case "false" -> null;
      default -> v;
    };
  }

  public static Path buildConfigPath(ConfigFlag configFlag, Path workDir) {
    if (runningInTestsAndNoCustomConfigPath(configFlag)) {
      return Files.createTempFileDeleteOnExit("dns-proxy-server-junit", ".json");
    }
    if (workDir != null) {
      return workDir
        .resolve(configFlag.getConfigPath())
        .toAbsolutePath()
        ;
    }
    final var confRelativeToCurrDir = Paths
      .get(configFlag.getConfigPath())
      .toAbsolutePath();
    if (Files.exists(confRelativeToCurrDir)) {
      return confRelativeToCurrDir;
    }
    return Runtime.getRunningDir()
      .resolve(configFlag.getConfigPath())
      .toAbsolutePath();
  }

  static Config build(ConfigFlag configFlag) {
    final var configEnv = ConfigEnv.fromEnv();
    final var configPath = buildConfigPath(configFlag, configEnv.getCurrentPath());
    final var jsonConfig = JsonConfigs.loadConfig(configPath);
    log.info("status=configuring, configFile={}", configPath);
    return build(configFlag, configEnv, jsonConfig, configPath);
  }

  static boolean runningInTestsAndNoCustomConfigPath(ConfigFlag configFlag) {
    return !Arrays.toString(configFlag.getArgs()).contains("--conf-path") && Tests.inTest();
  }

}
