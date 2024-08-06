package com.mageddo.dnsproxyserver.config.application;

import com.mageddo.commons.lang.Singletons;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.di.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Configs {

  private static final Context context = Context.create();

  public static Config getInstance() {
    return Singletons.createOrGet(Config.class, () -> {
      log.trace("status=cacheHotLoading");
      return context.configService().findCurrentConfig();
    });
  }

  public static void clear() {
    Singletons.clear(Config.class);
  }

  public static Context getContext() {
    return context;
  }
}
