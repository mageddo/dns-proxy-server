package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application;

public class HealthCheckerStatic implements HealthChecker {

  private final boolean healthy;

  public HealthCheckerStatic(boolean healthy) {
    this.healthy = healthy;
  }

  @Override
  public boolean isHealthy() {
    return this.healthy;
  }
}
