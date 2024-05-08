package com.mageddo.dnsproxyserver.config.entrypoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Validate;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.io.PrintWriter;
import java.net.URI;
import java.util.concurrent.Callable;


@Getter
@NoArgsConstructor
public class ConfigFlag implements Callable<Boolean> {

  @Option(
    names = {"-version", "--version"}, description = "Shows the current version (default false)"
  )
  private boolean version;

  @Option(
    names = {"-web-server-port", "--web-server-port"},
    description = "The web server port (default 5380)",
    defaultValue = "5380"
  )
  private Integer webServerPort;

  @Option(
    names = {"-server-port", "--server-port"},
    description = "The DNS server to start into (default 53)",
    defaultValue = "53"
  )
  private Integer dnsServerPort;

  @Option(
    names = {"-default-dns", "--default-dns"},
    description = "This DNS server will be the default server for this machine (default true)",
    defaultValue = "true"
  )
  private Boolean defaultDns;

  @Option(
    names = {"-conf-path", "--conf-path"},
    description = "The config file path (default conf/config.json)",
    defaultValue = "conf/config.json"
  )
  private String configPath;

  @Option(
    names = {"-service", "--service"},
    description = """
      Setup as service, starting with machine at boot
         docker = start as docker service,
         normal = start as normal service,
         uninstall = uninstall the service from machine
      (default <empty>)
      """
  )
  private String service;

  @Option(
    names = {"-service-publish-web-port", "--service-publish-web-port"},
    description = "Publish web port when running as service in docker mode (default true)",
    defaultValue = "true"
  )
  private Boolean publishServicePort;

  @Option(
    names = {"-log-file", "--log-file"},
    description = """
      Log to file instead of console,
      (true=log to default log file, /tmp/log.log=log to custom log location)
      (default console)
      """,
    defaultValue = "console"
  )
  private String logToFile;

  @Option(
    names = {"-log-level", "--log-level"},
    description = "Log Level ERROR, WARNING, INFO, DEBUG (default INFO)",
    defaultValue = "INFO"
  )
  private String logLevel;

  @Option(
    names = {"-register-container-names", "--register-container-names"},
    description = "If must register container name / service name as host in DNS server (default false)",
    defaultValue = "false"
  )
  private Boolean registerContainerNames;

  @Option(
    names = {"-host-machine-hostname", "--host-machine-hostname"},
    description = "The hostname to get host machine IP (default host.docker)",
    defaultValue = "host.docker"
  )
  private String hostMachineHostname;

  @Option(
    names = {"-domain", "--domain"},
    description = "Domain utilized to solver containers and services hostnames (default docker)",
    defaultValue = "docker"
  )
  private String domain;

  @Option(
    names = {"-dps-network", "--dps-network"},
    description = "Create a bridge network for DPS increasing compatibility (default false)",
    defaultValue = "false"
  )
  private Boolean dpsNetwork;

  @Option(
    names = {"-dps-network-auto-connect", "--dps-network-auto-connect"},
    description = """
      Connect all running and new containers to the DPS network,
      this way you will probably not have resolution issues by acl (implies dps-network=true)
      (default false)
       """,
    defaultValue = "false"
  )
  private Boolean dpsNetworkAutoConnect;

  @Option(
    names = {"-docker-host", "--docker-host"},
    description = """
      The docker host address.
      (default the default docker host value based on the OS)
       """
  )
  private URI dockerHost;

  @Option(
    names = {"-resolvconf-override-name-servers", "--resolvconf-override-name-servers"},
    description = """
      If must comment all existing nameservers at resolv.conf file
      or just put DPS at the first place.
      (default true)
       """,
    defaultValue = "true"
  )
  private Boolean resolvConfOverrideNameServers;

  @Option(
    names = {"-no-remote-servers", "--no-remote-servers"},
    description = """
      If remote servers like 8.8.8.8 must be disabled and only local solvers like docker
      containers or local db must be used.
      (default false)
       """,
    defaultValue = "false"
  )
  private Boolean noRemoteServers;

  @Option(
    names = {"-no-entries-response-code", "--no-entries-response-code"},
    description = """
      Response code to use when no entries are returned by the configured solvers
      (default 3) which means NXDOMAIN
       """,
    defaultValue = "3"
  )
  private Integer noEntriesResponseCode;

  @Option(
    names = {"-help", "--help"},
    description = "This message (default false)",
    usageHelp = true
  )
  private Boolean help;

  @Option(
    names = {"-docker-solver-host-machine-fallback", "--docker-solver-host-machine-fallback"},
    description = """
      Whether should answer host machine IP when a matching container is found but it hasn't
      an IP to be answered. See Github Issue #442
      """,
    defaultValue = "true"
  )
  private Boolean dockerSolverHostMachineFallbackActive;

  @JsonIgnore
  private String[] args;

  @JsonIgnore
  private CommandLine commandLine;

  public static ConfigFlag parse(String[] args) {
    return parse(args, null);
  }

  public static ConfigFlag parse(String[] args, PrintWriter writer) {
    final var commandLine = new CommandLine(new ConfigFlag());

    if (writer != null) {
      commandLine.setOut(writer);
    }
    commandLine.setUsageHelpWidth(120);
    commandLine.setColorScheme(CommandLine.Help.defaultColorScheme(CommandLine.Help.Ansi.OFF));

    final var flags = (ConfigFlag) commandLine.getCommand();
    flags.args = args;
    flags.commandLine = commandLine;
    Validate.isTrue(commandLine.execute(args) == 0, "Execution Failed");

    final var shouldExit = (Boolean) flags.getCommandLine().getExecutionResult();
    if (shouldExit == null || shouldExit) {
      flags.getCommandLine().getOut().flush();
      return flags;
    }

    return flags;
  }

  /**
   * @return should exit program
   */
  @Override
  public Boolean call() {
    if (this.version) {
      this.commandLine.getOut().write(ConfigProps.getVersion());
      return true;
    }
    return false;
  }

}
