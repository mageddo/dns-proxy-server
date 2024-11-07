package com.mageddo.dnsproxyserver.config.dataprovider.vo;

import com.mageddo.dnsproxyserver.utils.Envs;
import com.mageddo.http.UriUtils;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
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

  public static final String MG_DOCKER_HOST = "MG_DOCKER_HOST";

  public static final String MG_RESOLVCONF_OVERRIDE_NAMESERVERS = "MG_RESOLVCONF_OVERRIDE_NAMESERVERS";

  public static final String MG_NO_REMOTE_SERVERS = "MG_NO_REMOTE_SERVERS";

  public static final String MG_NO_ENTRIES_RESPONSE_CODE = "MG_NO_ENTRIES_RESPONSE_CODE";

  public static final String MG_DOCKER_SOLVER_HOST_MACHINE_FALLBACK_ACTIVE = "MG_DOCKER_SOLVER_HOST_MACHINE_FALLBACK_ACTIVE";

  public static final String MG_CONFIG_FILE_PATH = "MG_CONFIG_FILE_PATH";

  public static final String MG_STUB_SOLVER_DOMAIN_NAME = "MG_STUB_SOLVER_DOMAIN_NAME";

  private Path workingDir;
  private String resolvConfPath;
  private String logFile;
  private String logLevel;
  private Boolean registerContainerNames;
  private String hostMachineHostname;
  private String domain;
  private Boolean dpsNetwork;
  private Boolean dpsNetworkAutoConnect;
  private URI dockerHost;
  private Boolean resolvConfOverrideNameServers;
  private Boolean noRemoteServers;
  private Integer noEntriesResponseCode;
  private Boolean dockerSolverHostMachineFallbackActive;
  private Path configFilePath;
  private String solverStubDomainName;

  public static ConfigEnv fromEnv() {
    return ConfigEnv
      .builder()
      .workingDir(Envs.getPathOrNull(MG_WORK_DIR))
      .resolvConfPath(Envs.getStringOrDefault(MG_RESOLVCONF, DEFAULT_RESOLV_CONF_PATH))
      .logFile(findLogFilePath())
      .logLevel(Envs.getStringOrNull(MG_LOG_LEVEL))
      .registerContainerNames(Envs.getBooleanOrNull(MG_REGISTER_CONTAINER_NAMES))
      .hostMachineHostname(Envs.getStringOrNull(MG_HOST_MACHINE_HOSTNAME))
      .domain(Envs.getStringOrNull(MG_HOST_MACHINE_HOSTNAME))
      .dpsNetwork(Envs.getBooleanOrNull(MG_DPS_NETWORK))
      .dpsNetworkAutoConnect(Envs.getBooleanOrNull(MG_DPS_NETWORK_AUTO_CONNECT))
      .dockerHost(UriUtils.createURI(Envs.getStringOrNull(MG_DOCKER_HOST)))
      .resolvConfOverrideNameServers(Envs.getBooleanOrNull(MG_RESOLVCONF_OVERRIDE_NAMESERVERS))
      .noRemoteServers(Envs.getBooleanOrNull(MG_NO_REMOTE_SERVERS))
      .noEntriesResponseCode(Envs.getIntegerOrNull(MG_NO_ENTRIES_RESPONSE_CODE))
      .dockerSolverHostMachineFallbackActive(Envs.getBooleanOrNull(MG_DOCKER_SOLVER_HOST_MACHINE_FALLBACK_ACTIVE))
      .configFilePath(Envs.getPathOrNull(MG_CONFIG_FILE_PATH))
      .solverStubDomainName(Envs.getStringOrNull(MG_STUB_SOLVER_DOMAIN_NAME))
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
