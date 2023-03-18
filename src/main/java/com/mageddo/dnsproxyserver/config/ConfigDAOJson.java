package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsproxyserver.config.entrypoint.ConfigJson;
import com.mageddo.dnsproxyserver.config.entrypoint.ConfigJsonV2;
import com.mageddo.dnsproxyserver.config.entrypoint.JsonConfigs;
import com.mageddo.dnsproxyserver.config.entrypoint.predicate.JsonEnvPredicate;
import com.mageddo.dnsproxyserver.config.predicate.EntryPredicate;
import com.mageddo.dnsproxyserver.config.predicate.EnvPredicate;
import com.mageddo.dnsproxyserver.server.dns.solver.HostnameQuery;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Slf4j
@Singleton
@NoArgsConstructor(onConstructor = @__({@Inject}))
public class ConfigDAOJson implements ConfigDAO {

  @Override
  public Config.Env findActiveEnv() {
    final var configJson = JsonConfigs.loadConfigJson();
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
  public Config.Entry findEntryForActiveEnv(HostnameQuery hostname) {
    final var env = this.findActiveEnv();
    return env.getEntries()
      .stream()
      .filter(it -> hostname.matches(it.getHostname()))
      .findFirst()
      .orElse(null);
  }

  @Override
  public void addEntry(String env, Config.Entry entry) {
    final var config = JsonConfigs.loadConfigJson();
    final var found = findOrBind(env, config);
    found.add(ConfigJsonV2.Entry.from(entry));
    save(config);
  }

  @Override
  public boolean updateEntry(String env, Config.Entry entry) {
    final var config = JsonConfigs.loadConfigJson();
    final var found = findOrBind(env, config);
    final var hostnames = found.getHostnames();
    if (hostnames.isEmpty()) {
      return false;
    }
    return findHostname(
      EntryPredicate.byId(entry.getId()),
      hostnames,
      (foundEntry, i, entries) -> {
        entries.set(i, ConfigJsonV2.Entry.from(entry));
        save(config);
      });
  }

  @Override
  public boolean removeEntry(String env, String hostname) {
    final var config = JsonConfigs.loadConfigJson();
    final var found = findOrBind(env, config);
    final var hostnames = found.getHostnames();
    if (hostnames.isEmpty()) {
      return false;
    }
    return findHostname(
      EntryPredicate.exactName(hostname),
      hostnames,
      (foundEntry, i, entries) -> {
        entries.remove((int) i);
        save(config);
      }
    );
  }

  @Override
  public void createEnv(Config.Env env) {
    final var config = JsonConfigs.loadConfigJson();

    final var alreadyExists = config
      .get_envs()
      .stream()
      .anyMatch(JsonEnvPredicate.byName(env.getName()));

    Validate.isTrue(!alreadyExists, "The '%s' env already exists", env.getName());

    config
      .get_envs()
      .add(ConfigJsonV2.Env.from(env))
    ;
    save(config);
  }

  @Override
  public void deleteEnv(String name) {
    final var config = JsonConfigs.loadConfigJson();
    final var filtered = config
      .get_envs()
      .stream()
      .filter(JsonEnvPredicate.nameIsNot(name))
      .toList();
    config.set_envs(filtered);
    save(config);
  }

  @Override
  public List<Config.Env> findEnvs() {
    return JsonConfigs.loadConfigJson().getEnvs();
  }

  @Override
  public List<Config.Entry> findHostnamesBy(String env, String hostname) {
    final var foundEnv = this.findEnv(env);
    if (foundEnv == null) {
      return null;
    }
    if (StringUtils.isBlank(hostname)) {
      return foundEnv.getEntries();
    }
    return foundEnv.getEntries()
      .stream()
      .filter(EntryPredicate.nameMatches(hostname))
      .toList();
  }

  @Override
  public void changeActiveEnv(String name) {
    final var config = JsonConfigs.loadConfigJson()
      .setActiveEnv(name);
    save(config);
  }

  ConfigJsonV2.Env findOrBind(String envKey, ConfigJsonV2 configJson) {
    for (final var env : configJson.get_envs()) {
      if (Objects.equals(env.getName(), envKey)) {
        log.trace("status=envFound, activeEnv={}", envKey);
        return env;
      }
    }
    log.debug("status=envNotFound, action=creating, activeEnv={}", envKey);
    final var def = ConfigJsonV2.Env.from(Config.Env.of(envKey, Collections.emptyList()));
    configJson.get_envs().add(def);
    return def;
  }

  static Config.Env findEnv(String envKey, final ConfigJson configJson) {
    final var env = configJson
      .getEnvs()
      .stream()
      .filter(EnvPredicate.byName(envKey))
      .findFirst()
      .orElse(Config.Env.theDefault());
    log.trace("activeEnv={}", env.getName());
    return env;
  }

  static void save(ConfigJsonV2 config) {
    final var configPath = Configs
      .getInstance()
      .getConfigPath();
    JsonConfigs.write(configPath, config);
  }

  static boolean findHostname(
    Predicate<ConfigJsonV2.Entry> p,
    List<ConfigJsonV2.Entry> hostnames,
    TriConsumer<ConfigJsonV2.Entry, Integer, List<ConfigJsonV2.Entry>> c
  ) {
    for (int i = 0; i < hostnames.size(); i++) {
      final var foundEntry = hostnames.get(i);
      if (p.test(foundEntry)) {
        c.accept(foundEntry, i, hostnames);
        log.debug("status=found, entryId={}, hostname={}", foundEntry.getId(), foundEntry.getHostname());
        return true;
      }
    }
    return false;
  }

  interface TriConsumer<A, B, C> {
    void accept(A a, B b, C c);
  }
}

