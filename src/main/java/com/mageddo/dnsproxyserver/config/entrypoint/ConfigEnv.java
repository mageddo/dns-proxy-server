package com.mageddo.dnsproxyserver.config.entrypoint;

import com.mageddo.dnsproxyserver.utils.Envs;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;

@Value
@Builder
public class ConfigEnv {

  public static final String MG_WORK_DIR = "MG_WORK_DIR";

  public static final String MG_RESOLVCONF = "MG_RESOLVCONF";
  public static final String MG_LOG_FILE = "MG_LOG_FILE";
  public static final String MG_LOG_LEVEL = "MG_LOG_LEVEL";

  /**
   * If must register container name / service name as hostname
   */
  public static final String MG_REGISTER_CONTAINER_NAMES = "MG_REGISTER_CONTAINER_NAMES";

  public static final String MG_HOST_MACHINE_HOSTNAME = "MG_HOST_MACHINE_HOSTNAME";
  public static final String MG_DOMAIN = "MG_DOMAIN";
  public static final String MG_DPS_NETWORK = "MG_DPS_NETWORK";
  public static final String MG_DPS_NETWORK_AUTO_CONNECT = "MG_DPS_NETWORK_AUTO_CONNECT";
  public static final String DEFAULT_RESOLV_CONF_PATH =
    "/host/etc/systemd/resolved.conf,/host/etc/resolv.conf,/etc/systemd/resolved.conf,/etc/resolv.conf";

  private Path currentPath;
  private String resolvConfPath;
  private String logFile;
  private String logLevel;
  private Boolean registerContainerNames;
  private String hostMachineHostname;
  private String domain;
  private Boolean dpsNetwork;
  private Boolean dpsNetworkAutoConnect;

  public static ConfigEnv fromEnv() {
    return ConfigEnv
      .builder()
      .currentPath(Envs.getPathOrNull(MG_WORK_DIR))
      .resolvConfPath(Envs.getStringOrDefault(MG_RESOLVCONF, DEFAULT_RESOLV_CONF_PATH))
      .logFile(findLogFilePath())
      .logLevel(Envs.getStringOrNull(MG_LOG_LEVEL))
      .registerContainerNames(Envs.getBooleanOrNull(MG_REGISTER_CONTAINER_NAMES))
      .hostMachineHostname(Envs.getStringOrNull(MG_HOST_MACHINE_HOSTNAME))
      .domain(Envs.getStringOrNull(MG_HOST_MACHINE_HOSTNAME))
      .dpsNetwork(Envs.getBooleanOrNull(MG_DPS_NETWORK))
      .dpsNetworkAutoConnect(Envs.getBooleanOrNull(MG_DPS_NETWORK_AUTO_CONNECT))
      .build();
  }

  static String findLogFilePath() {
    final var logFile = System.getenv(MG_LOG_FILE);
    if (StringUtils.isBlank(logFile)) {
      return null;
    }
    return logFile;
  }
}
