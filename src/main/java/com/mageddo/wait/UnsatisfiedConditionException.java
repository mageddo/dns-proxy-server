package com.mageddo.wait;

public class UnsatisfiedConditionException extends RuntimeException {
  public UnsatisfiedConditionException() {
  }

  public UnsatisfiedConditionException(Throwable t) {
    super(t);
  }
}
