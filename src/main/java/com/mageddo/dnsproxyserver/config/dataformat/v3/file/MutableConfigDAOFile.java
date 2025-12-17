package com.mageddo.dnsproxyserver.config.dataformat.v3.file;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.commons.lang.tuple.Pair;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Config.Env;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.config.dataprovider.MutableConfigDAO;
import com.mageddo.dnsproxyserver.config.mapper.ConfigMapper;
import com.mageddo.dnsproxyserver.config.predicate.EntryPredicate;
import com.mageddo.dnsproxyserver.config.predicate.EnvPredicate;
import com.mageddo.dnsproxyserver.solver.HostnameQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MutableConfigDAOFile implements MutableConfigDAO {

  private final ConfigFileDAO configFileDAO;

  @Override
  public Config find() {
    return Configs.getContext()
        .configService()
        .find();
  }

  Config findCached() {
    return Configs.getInstance();
  }

  @Override
  public Env findActiveEnv() {
    final var config = this.findCached();
    return findEnv(config.getActiveEnv(), config);
  }

  @Override
  public Env findEnv(String envKey) {
    return findEnv(envKey, this.find());
  }

  @Override
  public Config.Entry findEntryForActiveEnv(HostnameQuery query) {
    final var env = this.findActiveEnv();
    return env.getEntries()
        .stream()
        .filter(it -> query.matches(it.getHostname()))
        .min((o1, o2) -> {
          if (o1.getType() == o2.getType()) {
            return 0;
          }
          if (query.isTypeEqualTo(o1.getType())) {
            return -1;
          }
          return 1;
        })
        .orElse(null);
  }

  @Override
  public void addEntry(String env, Config.Entry entry) {
    final var config = this.find();
    this.save(ConfigMapper.replace(config, env, entry));
  }

  @Override
  public boolean updateEntry(String env, Config.Entry entry) {
    this.save(ConfigMapper.replace(this.find(), env, entry));
    return true;
  }

  @Override
  public boolean removeEntry(String env, String hostname) {
    final var config = ConfigMapper.remove(this.find(), env, hostname);
    if (config == null) {
      return false;
    }
    this.save(config);
    return true;
  }

  @Override
  public void createEnv(Env env) {
    final var config = this.find();
    final var alreadyExists = config.getEnvs()
        .stream()
        .anyMatch(EnvPredicate.byName(env.getName()));

    Validate.isTrue(!alreadyExists, "The '%s' env already exists", env.getName());

    config.getEnvs()
        .add(env)
    ;
    save(config);
  }

  @Override
  public void deleteEnv(String name) {
    final var config = this.find();
    final var filtered = config
        .getEnvs()
        .stream()
        .filter(EnvPredicate.nameIsNot(name))
        .toList();
    this.save(config.toBuilder()
        .solverLocal(config.getSolverLocal()
            .toBuilder()
            .envs(filtered)
            .build()
        )
        .build()
    );
  }

  @Override
  public List<Env> findEnvs() {
    return this.find()
        .getEnvs();
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
    final var sourceConfig = this.find();
    final var config = sourceConfig
        .toBuilder()
        .solverLocal(sourceConfig.getSolverLocal()
            .toBuilder()
            .activeEnv(name)
            .build()
        )
        .build();
    this.save(config);
  }

  Pair<Env, Config> findOrBind(String envKey, Config config) {
    for (final var env : config.getEnvs()) {
      if (Objects.equals(env.getName(), envKey)) {
        log.trace("status=envFound, activeEnv={}", envKey);
        return Pair.of(env, config);
      }
    }
    log.debug("status=envNotFound, action=creating, activeEnv={}", envKey);
    final var def = Env.of(envKey, Collections.emptyList());
    return Pair.of(def, ConfigMapper.add(config, def));
  }

  static Env findEnv(String envKey, final Config config) {
    final var env = config
        .getEnvs()
        .stream()
        .filter(EnvPredicate.byName(envKey))
        .findFirst()
        .orElse(Env.theDefault());
    log.trace("activeEnv={}", env.getName());
    return env;
  }

  void save(Config config) {
    Configs.clear();
    this.configFileDAO.save(config);
  }

  static boolean findHostname(
      Predicate<Config.Entry> p,
      List<Config.Entry> hostnames,
      TriConsumer<Config.Entry, Integer, List<Config.Entry>> c
  ) {
    for (int i = 0; i < hostnames.size(); i++) {
      final var foundEntry = hostnames.get(i);
      if (p.test(foundEntry)) {
        c.accept(foundEntry, i, hostnames);
        log.debug("status=found, entryId={}, hostname={}", foundEntry.getId(),
            foundEntry.getHostname()
        );
        return true;
      }
    }
    return false;
  }

  interface TriConsumer<A, B, C> {
    void accept(A a, B b, C c);
  }

}

