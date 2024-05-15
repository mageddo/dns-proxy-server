package com.mageddo.dnsproxyserver.config.application;

import com.mageddo.commons.lang.Singletons;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.mapper.DataproviderVoToConfigDomainMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Configs {

  public static Config getInstance() {
    return getInstance(new String[]{});
  }

  public static Config getInstance(String[] args) {
    final Config v = Singletons.get(Config.class);
    if (v != null) {
      return v;
    } else {
      return Singletons.createOrGet(Config.class, () -> DataproviderVoToConfigDomainMapper.build(args));
    }
  }

  public static void clear() {
    Singletons.clear(Config.class);
  }


}
