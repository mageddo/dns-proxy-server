package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.statetransitor;

public interface StateTransitor {
  void closed();
  void halfOpen();
}
