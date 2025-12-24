package com.mageddo.dnsproxyserver.solver.remote;

public enum CircuitStatus {
  OPEN,
  CLOSED,
  HALF_OPEN;

  public static boolean isOpen(CircuitStatus status) {
    return OPEN == status;
  }

  public static boolean isNotOpen(CircuitStatus status) {
    return !isOpen(status);
  }
}
