package com.mageddo.supporting.circuitbreaker.testing.sandbox.failsafe;

import com.mageddo.supporting.circuitbreaker.testing.sandbox.State;

import dev.failsafe.CircuitBreaker;

public class StateMapper {

  public static State from(CircuitBreaker.State state) {
    return State.valueOf(state.name());
  }
}
