package com.mageddo.dnsproxyserver.config.dataprovider.mapper;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJsonV2;
import com.mageddo.net.IP;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ConfigJsonV2EnvsMapper {

  public static List<Config.Env> toDomainEnvs(List<ConfigJsonV2.Env> envs) {
    return envs.stream()
      .map(ConfigJsonV2EnvsMapper::toDomainEnv)
      .toList();
  }

  public static Config.Env toDomainEnv(ConfigJsonV2.Env env) {
    return new Config.Env(env.getName(), ConfigJsonV2EnvsMapper.toDomainEntries(env.getHostnames()));
  }

  public static List<Config.Entry> toDomainEntries(List<ConfigJsonV2.Entry> hostnames) {
    if (hostnames == null) {
      return null;
    }
    return hostnames
      .stream()
      .map(ConfigJsonV2EnvsMapper::toDomainEntry)
      .toList();
  }

  public static Config.Entry toDomainEntry(ConfigJsonV2.Entry hostname) {
    return Config.Entry
      .builder()
      .hostname(hostname.getHostname())
      .id(hostname.getId())
      .ttl(hostname.getTtl())
      .ip(IP.of(hostname.getIp()))
      .target(hostname.getTarget())
      .type(buildType(hostname))
      .build();
  }

  private static Config.Entry.Type buildType(ConfigJsonV2.Entry hostname) {
    if (hostname.getType() != null) {
      return hostname.getType();
    }
    if (StringUtils.isNotBlank(hostname.getIp())) {
      return Config.Entry.Type.A;
    } else if (StringUtils.isNotBlank(hostname.getTarget())) {
      return Config.Entry.Type.CNAME;
    }
    throw new IllegalArgumentException("You must set the hostname type field, then fill target or ip field");
  }


}
