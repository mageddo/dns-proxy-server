package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.di.Eager;
import com.mageddo.dnsproxyserver.healthcheck.entrypoint.HealthCheckSignalEntrypoint;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

import javax.inject.Singleton;
import java.util.Set;

@Module
public class ModuleEager {
  @Provides
  @Singleton
  @ElementsIntoSet
  Set<Eager> beans(
      HealthCheckSignalEntrypoint b1
  ) {
    return Set.of(
      b1
    );
  }
}
