package com.mageddo.dnsproxyserver.solver.application;

import dev.failsafe.event.CircuitBreakerStateChangedEvent;

public class CircuitBreakerStateMapper {
  public static String toStateNameFrom(CircuitBreakerStateChangedEvent event) {
    return event.getPreviousState().name();
  }
}
