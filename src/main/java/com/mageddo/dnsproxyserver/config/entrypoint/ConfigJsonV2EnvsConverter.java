package com.mageddo.dnsproxyserver.config.entrypoint;

import com.mageddo.dnsproxyserver.config.Config;

import java.util.List;

public class ConfigJsonV2EnvsConverter {

  static List<Config.Env> toDomainEnvs(List<ConfigJsonV2.Env> envs) {
    return envs.stream()
      .map(ConfigJsonV2EnvsConverter::toDomainEnv)
      .toList();
  }

  static Config.Env toDomainEnv(ConfigJsonV2.Env env) {
    return new Config.Env(env.getName(), ConfigJsonV2EnvsConverter.toDomainEntries(env.getHostnames()));
  }

  static List<Config.Entry> toDomainEntries(List<ConfigJsonV2.Hostname> hostnames) {
    return hostnames
      .stream()
      .map(ConfigJsonV2EnvsConverter::toDomainEntry)
      .toList();
  }

  static Config.Entry toDomainEntry(ConfigJsonV2.Hostname hostname) {
    return Config.Entry
      .builder()
      .hostname(hostname.getHostname())
      .id(hostname.getId())
      .ttl(hostname.getTtl())
      .ip(hostname.getIp())
      .target(hostname.getTarget())
      .type(hostname.getType())
      .build();
  }


}
