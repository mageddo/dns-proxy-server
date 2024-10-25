package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.statetransitor;

import dev.failsafe.CircuitBreaker;

public class FailSafeStateTransitor implements StateTransitor {

  private final CircuitBreaker<?> circuitBreaker;

  public FailSafeStateTransitor(CircuitBreaker<?> circuitBreaker) {
    this.circuitBreaker = circuitBreaker;
  }

  @Override
  public void closed() {
    this.circuitBreaker.close();
  }

  @Override
  public void halfOpen() {
    this.circuitBreaker.halfOpen();
  }
}
