package com.mageddo.dnsproxyserver.config.entrypoint;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.net.IpAddr;

import java.util.List;

public class ConfigJsonV1EnvsConverter {

  static List<Config.Env> toDomainEnvs(List<ConfigJsonV1.Env> envs) {
    return envs.stream()
      .map(ConfigJsonV1EnvsConverter::toDomainEnv)
      .toList();
  }

  static Config.Env toDomainEnv(ConfigJsonV1.Env env) {
    return new Config.Env(env.getName(), ConfigJsonV1EnvsConverter.toDomainEntries(env.getEntries()));
  }

  static List<Config.Entry> toDomainEntries(List<ConfigJsonV1.Entry> hostnames) {
    return hostnames
      .stream()
      .map(ConfigJsonV1EnvsConverter::toDomainEntry)
      .toList();
  }

  static Config.Entry toDomainEntry(ConfigJsonV1.Entry entry) {
    return Config.Entry
      .builder()
      .hostname(entry.getHostname())
      .id(entry.getId())
      .ttl(entry.getTtl())
      .ip(IpAddr.of(entry.getIp()).getRawIP())
      .type(Config.Entry.Type.A)
      .build();
  }


}
