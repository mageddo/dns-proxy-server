package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsproxyserver.config.entrypoint.JsonConfigs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class ConfigDAOJson implements ConfigDAO {

  @Override
  public Config.Env findActiveEnv(){
    final var configPath = Configs
      .getInstance()
      .getConfigPath()
      ;
    final var configJson = JsonConfigs.loadConfig(configPath);
    final var activeEnvKey = configJson.getActiveEnv();
    final var env = configJson
      .getEnvs()
      .stream()
      .filter(it -> Objects.equals(it.getName(), activeEnvKey))
      .findFirst()
      .orElse(Config.Env.theDefault());
    log.debug("activeEnv={}", env.getName());
    return env;
  }

  @Override
  public Config.Entry findEntryForActiveEnv(String hostname) {
    final var env = this.findActiveEnv();
    return env.getEntries()
      .stream()
      .filter(it -> Objects.equals(it.getHostname(), hostname))
      .findFirst()
      .orElse(null);
  }
}
