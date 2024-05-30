package com.mageddo.dnsproxyserver.config.dataprovider;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.solver.HostnameQuery;

import java.util.List;

public interface PersistentConfigDAO {

  Config findActive();

  Config.Env findActiveEnv();

  Config.Env findEnv(String env);

  Config.Entry findEntryForActiveEnv(HostnameQuery hostname);

  void addEntry(String env, Config.Entry entry);

  List<Config.Env> findEnvs();

  /**
   * Find by env and/or hostname
   */
  List<Config.Entry> findHostnamesBy(String env, String hostname);

  void changeActiveEnv(String name);

  boolean updateEntry(String env, Config.Entry entry);

  boolean removeEntry(String env, String hostname);

  void createEnv(Config.Env env);

  void deleteEnv(String name);
}
