package com.mageddo.dnsproxyserver.config;

public interface ConfigDAO {

  Config.Env findActiveEnv();

  Config.Env findEnv(String env);

  Config.Entry findEntryForActiveEnv(String hostname);

  void addEntry(String env, Config.Entry entry);
}
