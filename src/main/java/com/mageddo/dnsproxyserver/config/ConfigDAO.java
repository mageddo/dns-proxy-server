package com.mageddo.dnsproxyserver.config;

public interface ConfigDAO {

  Config.Env findActiveEnv();

  Config.Entry findEntryForActiveEnv(String hostname);
}
