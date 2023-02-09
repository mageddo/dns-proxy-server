package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsproxyserver.config.entrypoint.ConfigEnv;
import com.mageddo.dnsproxyserver.config.entrypoint.ConfigFlag;
import com.mageddo.dnsproxyserver.config.entrypoint.ConfigJson;
import com.mageddo.dnsproxyserver.config.entrypoint.ConfigProps;
import com.mageddo.dnsproxyserver.config.entrypoint.JsonConfigs;
import com.mageddo.dnsproxyserver.config.entrypoint.LogLevel;
import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import com.mageddo.dnsproxyserver.utils.Numbers;
import com.mageddo.utils.Files;
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

  private static Config instance;

  public static Config build(ConfigFlag configFlag) {
    final var configPath = toAbsolutePath(configFlag).toAbsolutePath();
    final var jsonConfig = JsonConfigs.loadConfig(configPath);
    log.info("status=configuring, configFile={}", configPath);
    return build(configFlag, ConfigEnv.fromEnv(), jsonConfig, configPath);
  }

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
      .resolvConfPath(env.getResolvConfPath())
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

  public static Config buildAndRegister(String[] args) {
    final var config = ConfigFlag.parse(args);
    if (BooleanUtils.isTrue(config.getHelp()) || config.isVersion()) {
      System.exit(0);
    }
    return buildAndRegister(config);
  }

  public static Config buildAndRegister(ConfigFlag flag) {
    return instance = build(flag);
  }

  public static Config getInstance() {
    return instance != null ? instance : buildAndRegister(new String[]{});
  }

  public static void clear(){
    instance = null;
  }

  private static Path toAbsolutePath(ConfigFlag configFlag) {
    if (runningInTestsAndNoCustomConfigPath(configFlag)) {
      return Files.createTempFileExitOnExit("dns-proxy-server-junit", ".json");
    }
    return Paths.get(configFlag.getConfigPath()); // todo precisa converter para absolute path?!
  }

  static boolean runningInTestsAndNoCustomConfigPath(ConfigFlag configFlag) {
    return !Arrays.toString(configFlag.getArgs()).contains("--conf-path") && Tests.runningOnJunit();
  }

}
