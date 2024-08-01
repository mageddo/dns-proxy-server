package com.mageddo.circuitbreaker.failsafe;

import dev.failsafe.CircuitBreaker;

public class CircuitStatusRefresh {
  public static boolean refresh(CircuitBreaker<?> circuitBreaker) {
    if (readyToChangeToHalOpen(circuitBreaker)) {
      circuitBreaker.halfOpen();
      return true;
    }
    return false;
  }

  private static boolean readyToChangeToHalOpen(CircuitBreaker<?> circuitBreaker) {
    return circuitBreaker.isOpen() && circuitBreaker.getRemainingDelay().isZero();
  }
}
