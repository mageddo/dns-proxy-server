package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.config.Config;

import java.nio.file.Paths;

public class ConfigTemplates {
  public static Config withoutId() {
    return Config
      .builder()
      .logFile("/tmp/dps.log")
      .defaultDns(false)
      .dpsNetworkAutoConnect(false)
      .hostMachineHostname("host.docker")
      .configPath(Paths.get("/tmp/config.json"))
      .registerContainerNames(false)
      .dpsNetwork(false)
      .webServerPort(8080)
      .version("3.0.0")
      .dnsServerPort(53)
      .domain("com")
      .build();
  }
}
