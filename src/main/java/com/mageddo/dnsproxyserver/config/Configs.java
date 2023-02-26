package com.mageddo.dnsproxyserver.config;

import com.mageddo.commons.lang.Singletons;
import com.mageddo.dnsproxyserver.config.entrypoint.ConfigEnv;
import com.mageddo.dnsproxyserver.config.entrypoint.ConfigFlag;
import com.mageddo.dnsproxyserver.config.entrypoint.ConfigJson;
import com.mageddo.dnsproxyserver.config.entrypoint.ConfigProps;
import com.mageddo.dnsproxyserver.config.entrypoint.JsonConfigs;
import com.mageddo.dnsproxyserver.config.entrypoint.LogLevel;
import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import com.mageddo.dnsproxyserver.utils.Numbers;
import com.mageddo.utils.Files;
import com.mageddo.utils.Runtime;
import com.mageddo.utils.Tests;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mageddo.dnsproxyserver.utils.ObjectUtils.firstNonBlankRequiring;
import static com.mageddo.dnsproxyserver.utils.ObjectUtils.firstNonNullRequiring;

@Slf4j
public class Configs {

  public static Config build(ConfigFlag flag, ConfigEnv env, ConfigJson json, Path configPath) {
    return Config.builder()
      .version(ConfigProps.getVersion())
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
      .dpsNetwork(firstNonNullRequiring(
        env.getDpsNetwork(), json.getDpsNetwork(), flag.getDpsNetwork()
      ))
      .dpsNetworkAutoConnect(firstNonNullRequiring(
        env.getDpsNetworkAutoConnect(), json.getDpsNetworkAutoConnect(), flag.getDpsNetworkAutoConnect()
      ))
      .remoteDnsServers(buildRemoteServers(json.getRemoteDnsServers()))
      .configPath(configPath)
      .resolvConfPaths(env.getResolvConfPath())
      .build();
  }

  static List<IpAddr> buildRemoteServers(List<IpAddr> servers) {
    if (servers == null || servers.isEmpty()) {
      return Collections.singletonList(IpAddr.of("8.8.8.8:53"));
    }
    return servers;
  }

  static LogLevel buildLogLevel(String logLevelName) {
    return EnumUtils.getEnumIgnoreCase(LogLevel.class, logLevelName);
  }

  public static String parseLogFile(String v) {
    return switch (StringUtils.lowerCase(v)) {
      case "true" -> "/var/log/dns-proxy-server.log";
      case "false" -> null;
      default -> v;
    };
  }

  public static Config getInstance() {
    return getInstance(new String[]{});
  }

  public static Config getInstance(String[] args) {
    final Config v = Singletons.get(Config.class);
    if (v != null) {
      return v;
    } else {
      return Singletons.createOrGet(Config.class, () -> build(args));
    }
  }

  public static void clear() {
    Singletons.clear(Config.class);
  }

  /**
   * @see #getInstance(String[])
   */
  public static Config build(String[] args) {
    final var config = ConfigFlag.parse(args);
    if (BooleanUtils.isTrue(config.getHelp()) || config.isVersion()) {
      System.exit(0);
    }
    return build(config);
  }

  static Path buildConfigPath(ConfigFlag configFlag, Path workDir) {
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
