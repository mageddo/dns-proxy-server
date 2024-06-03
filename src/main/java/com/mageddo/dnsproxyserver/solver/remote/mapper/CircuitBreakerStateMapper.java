package com.mageddo.dnsproxyserver.solver.remote.mapper;

import dev.failsafe.event.CircuitBreakerStateChangedEvent;

public class CircuitBreakerStateMapper {
  public static String toStateNameFrom(CircuitBreakerStateChangedEvent event) {
    return event.getPreviousState().name();
  }
}
