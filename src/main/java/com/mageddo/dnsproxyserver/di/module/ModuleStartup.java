package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.dnsproxyserver.di.StartupEvent;
import com.mageddo.dnsproxyserver.dnsconfigurator.DnsConfigurators;
import com.mageddo.dnsproxyserver.solver.docker.entrypoint.EventListener;
import com.mageddo.dnsproxyserver.solver.remote.configurator.CircuitBreakerWatchDogScheduler;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

import javax.inject.Singleton;
import java.util.Set;

@Module
public interface ModuleStartup {

  @Provides
  @Singleton
  @ElementsIntoSet
  static Set<StartupEvent> startupBeans(
    DnsConfigurators b1, EventListener b2, CircuitBreakerWatchDogScheduler b3
  ){
    return Set.of(b1, b2, b3);
  }

}
