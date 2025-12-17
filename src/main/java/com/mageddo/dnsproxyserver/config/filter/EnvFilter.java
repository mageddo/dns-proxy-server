package com.mageddo.dnsproxyserver.config.filter;

import java.util.Objects;

import com.mageddo.dnsproxyserver.config.Config;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnvFilter {
  public static Config.Env filter(Config config, String envKey) {
    for (final var env : config.getEnvs()) {
      if (Objects.equals(env.getName(), envKey)) {
        log.trace("status=envFound, activeEnv={}", envKey);
        return env;
      }
    }
    return null;
  }
}
