package com.mageddo.dnsproxyserver.config;

import java.util.List;

public interface ConfigDAO {

  Config.Env findActiveEnv();

  Config.Env findEnv(String env);

  Config.Entry findEntryForActiveEnv(String hostname);

  void addEntry(String env, Config.Entry entry);

  List<Config.Env> findEnvs();

  List<Config.Entry> findHostnamesBy(String env, String hostname);

  void changeActiveEnv(String name);

  boolean updateEntry(String env, Config.Entry entry);

  boolean removeEntry(String env, String hostname);

  void createEnv(Config.Env env);

  void deleteEnv(String name);
}
