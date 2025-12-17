package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application;

import java.util.function.Supplier;

import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.statetransitor.StateTransitor;

public interface CircuitBreakerDelegate {

  Result execute(Supplier<Result> sup);

  CircuitStatus findStatus();

  StateTransitor stateTransitor();

  default void transitionToHalfOpenState() {
    this.stateTransitor()
        .halfOpen();
  }

  default void transitionToClosedState() {
    this.stateTransitor()
        .closed();
  }
}
