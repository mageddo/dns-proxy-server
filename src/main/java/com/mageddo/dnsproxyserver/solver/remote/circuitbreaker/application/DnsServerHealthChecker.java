package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application;

import com.mageddo.net.IpAddr;

public class DnsServerHealthChecker implements HealthChecker {

  public DnsServerHealthChecker(IpAddr addr) {

  }

  @Override
  public boolean isHealthy() {
    return false;
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
