package testing.templates.solver.remote;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import dev.failsafe.CircuitBreaker;

import java.time.Duration;

public class FailSafeCircuitBreakerTemplates {
  public static CircuitBreaker<Result> buildDefault() {
    return CircuitBreaker.<Result>builder()
      .handle(CircuitCheckException.class)
      .withFailureThreshold(1)
      .withSuccessThreshold(2)
      .build();
  }

  public static <T> CircuitBreaker<T> fastCircuit() {
    return CircuitBreaker.<T>builder()
      .handle(CircuitCheckException.class)
      .withFailureThreshold(1)
      .withSuccessThreshold(1)
      .withDelay(Duration.ofMillis(50))
      .build();
  }

  public static <T> CircuitBreaker<T> fastOpenCircuit() {
    final CircuitBreaker<T> circuit = fastCircuit();
    circuit.open();
    return circuit;
  }
}
