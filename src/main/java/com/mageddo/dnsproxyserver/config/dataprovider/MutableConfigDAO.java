package com.mageddo.dnsproxyserver.config.dataprovider;

import java.util.List;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.solver.HostnameQuery;

public interface MutableConfigDAO {

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
