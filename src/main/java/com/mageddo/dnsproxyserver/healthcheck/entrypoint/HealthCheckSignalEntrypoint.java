package com.mageddo.dnsproxyserver.healthcheck.entrypoint;

import com.mageddo.di.Eager;
import com.mageddo.dnsproxyserver.healthcheck.HealthCheck;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Signal;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class HealthCheckSignalEntrypoint implements Eager {

  private static final String USER_DEFINED_SIGNAL = "USR1";
  private final HealthCheck healthCheck;

  @Inject
  public HealthCheckSignalEntrypoint(HealthCheck healthCheck) {
    this.healthCheck = healthCheck;
  }

  @Override
  public void run() {
    this.safeRegisterHandler();
  }

  void safeRegisterHandler() {
    try {
      this.registerHandler();
    } catch (Throwable t){
      log.warn("status=couldntRegisterHandler, msg={}", t.getMessage(), t);
    }
  }

  void registerHandler() {
    Signal.handle(new Signal(USER_DEFINED_SIGNAL), sig -> {
      System.out.printf("dps.healthCheck.health=%b%n", this.healthCheck.isHealth());
    });
    log.debug("status=healthCheckSignalRegistered, signal={}", USER_DEFINED_SIGNAL);
  }
}
