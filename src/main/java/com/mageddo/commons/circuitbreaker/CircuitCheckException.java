package com.mageddo.commons.circuitbreaker;

public class CircuitCheckException extends RuntimeException {
  public CircuitCheckException(String message) {
    super(message);
  }

  public CircuitCheckException(Throwable e) {
    super(e.getMessage(), e);
  }
}
