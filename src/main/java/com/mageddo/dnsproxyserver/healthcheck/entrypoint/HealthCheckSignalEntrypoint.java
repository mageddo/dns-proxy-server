package com.mageddo.dnsproxyserver.healthcheck.entrypoint;

import com.mageddo.di.Eager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Signal;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class HealthCheckSignalEntrypoint implements Eager {

  private static final String USER_DEFINED_SIGNAL = "USR1";
  private final HealthCheckStreamEntrypoint streamEntrypoint;

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
    Signal.handle(new Signal(USER_DEFINED_SIGNAL), sig -> this.streamEntrypoint.printHealthCheck());
    log.debug("status=healthCheckSignalRegistered, signal={}", USER_DEFINED_SIGNAL);
  }
}
