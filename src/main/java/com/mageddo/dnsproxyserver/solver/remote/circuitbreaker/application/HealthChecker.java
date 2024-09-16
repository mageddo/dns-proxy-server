package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application;

public interface HealthChecker {
  boolean isHealthy();
}
