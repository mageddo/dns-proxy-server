package com.mageddo.commons.circuitbreaker;

public class CircuitCheckException extends RuntimeException {
  public CircuitCheckException(String message) {
    super(message);
  }
}
