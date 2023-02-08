package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsproxyserver.config.entrypoint.ConfigJson;
import com.mageddo.dnsproxyserver.config.entrypoint.ConfigJsonV2;
import com.mageddo.dnsproxyserver.config.entrypoint.JsonConfigs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class ConfigDAOJson implements ConfigDAO {

  @Override
  public Config.Env findActiveEnv() {
    final var configJson = findConfigJson();
    return findEnv(configJson.getActiveEnv(), configJson);
  }

  @Override
  public Config.Env findEnv(String envKey) {
    final var configPath = Configs
      .getInstance()
      .getConfigPath();
    return findEnv(envKey, JsonConfigs.loadConfig(configPath));
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

  @Override
  public void addEntry(String env, Config.Entry entry) {

    final var configPath = Configs
      .getInstance()
      .getConfigPath();

    final var config = (ConfigJsonV2) JsonConfigs.loadConfig(configPath);
    final var found = findOrBind(env, config);
    found.add(ConfigJsonV2.Entry.from(entry));
    JsonConfigs.write(configPath, config);

  }

  @Override
  public List<Config.Env> findEnvs() {
    return findConfigJson().getEnvs();
  }

  @Override
  public List<Config.Entry> findHostnamesBy(String env, String hostname) {
    final var foundEnv = this.findEnv(env);
    if (foundEnv == null) {
      return null;
    }
    if(StringUtils.isBlank(hostname)){
      return foundEnv.getEntries();
    }
    return foundEnv.getEntries()
      .stream()
      .filter(it -> it.getHostname().matches(String.format(".*%s.*", hostname)))
      .toList();
  }

  ConfigJsonV2.Env findOrBind(String envKey, ConfigJsonV2 configJson) {
    for (final var env : configJson.get_envs()) {
      if (Objects.equals(env.getName(), envKey)) {
        log.debug("status=envFound, activeEnv={}", envKey);
        return env;
      }
    }
    log.debug("status=notFound, action=usingDefaultEnv, activeEnv={}", Config.Env.DEFAULT_ENV);
    final var def = ConfigJsonV2.Env.from(Config.Env.theDefault());
    configJson.get_envs().add(def);
    return def;
  }

  static Config.Env findEnv(String envKey, final ConfigJson configJson) {
    final var env = configJson
      .getEnvs()
      .stream()
      .filter(it -> Objects.equals(it.getName(), envKey))
      .findFirst()
      .orElse(Config.Env.theDefault());
    log.debug("activeEnv={}", env.getName());
    return env;
  }

  static ConfigJson findConfigJson() {
    final var configPath = Configs
      .getInstance()
      .getConfigPath();
    return JsonConfigs.loadConfig(configPath);
  }
}

