package com.mageddo.dnsproxyserver.solver.remote.mapper;

import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import dev.failsafe.CircuitBreaker;
import dev.failsafe.event.CircuitBreakerStateChangedEvent;

public class CircuitBreakerStateMapper {

  public static CircuitStatus toStateNameFrom(CircuitBreakerStateChangedEvent event) {
    return fromFailSafeState(event.getPreviousState());
  }

  public static CircuitStatus fromFailSafeState(CircuitBreaker.State state) {
    return CircuitStatus.valueOf(state.name());
  }

  public static CircuitStatus fromFailSafeCircuitBreaker(CircuitBreaker<Result> circuitBreaker) {
    if (circuitBreaker == null) {
      return null;
    }
    return fromFailSafeState(circuitBreaker.getState());
  }
}
