package com.mageddo.dnsproxyserver.config.dataprovider.mapper;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJsonV1;
import com.mageddo.net.IpAddr;

import java.util.List;

public class ConfigJsonV1EnvsMapper {

  public static List<Config.Env> toDomainEnvs(List<ConfigJsonV1.Env> envs) {
    return envs.stream()
      .map(ConfigJsonV1EnvsMapper::toDomainEnv)
      .toList();
  }

  public static Config.Env toDomainEnv(ConfigJsonV1.Env env) {
    return new Config.Env(env.getName(), ConfigJsonV1EnvsMapper.toDomainEntries(env.getEntries()));
  }

  public static List<Config.Entry> toDomainEntries(List<ConfigJsonV1.Entry> hostnames) {
    return hostnames
      .stream()
      .map(ConfigJsonV1EnvsMapper::toDomainEntry)
      .toList();
  }

  public static Config.Entry toDomainEntry(ConfigJsonV1.Entry entry) {
    return Config.Entry
      .builder()
      .hostname(entry.getHostname())
      .id(entry.getId())
      .ttl(entry.getTtl())
      .ip(IpAddr.of(entry.getIp()).getIp())
      .type(Config.Entry.Type.A)
      .build();
  }


}
