package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.statetransitor;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

public class Resilience4jStateTransitor implements StateTransitor {

  private final CircuitBreaker circuitBreaker;

  public Resilience4jStateTransitor(CircuitBreaker circuitBreaker) {
    this.circuitBreaker = circuitBreaker;
  }

  @Override
  public void closed() {
    this.circuitBreaker.transitionToClosedState();
  }

  @Override
  public void halfOpen() {
    this.circuitBreaker.transitionToHalfOpenState();
  }
}
