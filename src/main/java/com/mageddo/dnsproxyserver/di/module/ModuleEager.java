package com.mageddo.dnsproxyserver.di.module;

import java.util.Set;

import javax.inject.Singleton;

import com.mageddo.di.Eager;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.entrypoint.CircuitBreakerHeater;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

@Module
public class ModuleEager {
  @Provides
  @Singleton
  @ElementsIntoSet
  Set<Eager> beans(CircuitBreakerHeater a) {
    return Set.of(
        a
    );
  }
}
