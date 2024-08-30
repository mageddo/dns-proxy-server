package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker;

public class CircuitIsOpenException extends RuntimeException {
  public CircuitIsOpenException(Throwable e) {
    super(e);
  }
}
