package com.mageddo.commons.circuitbreaker;

public class CircuitIsOpenException extends RuntimeException {
  public CircuitIsOpenException(Throwable e) {
    super(e);
  }
}
