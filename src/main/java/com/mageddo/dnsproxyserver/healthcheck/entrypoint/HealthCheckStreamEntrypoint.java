package com.mageddo.dnsproxyserver.healthcheck.entrypoint;

import com.mageddo.dnsproxyserver.healthcheck.HealthCheck;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class HealthCheckStreamEntrypoint {

  private final HealthCheck healthCheck;

  public void printHealthCheck() {
    System.out.printf("dps.healthCheck.health=%b%n", this.healthCheck.isHealth());
  }
}
