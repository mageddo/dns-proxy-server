package testing.templates.circuitbreaker;

import java.io.UncheckedIOException;
import java.time.Duration;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Resilience4jCircuitBreakerTemplates {


  public static CircuitBreaker theDefault() {
    return CircuitBreaker.of(
        "defaultCircuitBreaker",
        CircuitBreakerConfig
            .custom()

            .failureRateThreshold(21f)
            .minimumNumberOfCalls(100)
            .permittedNumberOfCallsInHalfOpenState(10)

            .waitDurationInOpenState(Duration.ofDays(365))
            .recordExceptions(CircuitCheckException.class)

            .build()
    );
  }

  public static CircuitBreaker theDefaultHandlingIoException() {
    return CircuitBreaker.of(
        "defaultCircuitBreaker",
        CircuitBreakerConfig
            .custom()

            .failureRateThreshold(21f)
            .minimumNumberOfCalls(100)
            .permittedNumberOfCallsInHalfOpenState(10)

            .waitDurationInOpenState(Duration.ofDays(365))
            .recordExceptions(UncheckedIOException.class)

            .build()
    );
  }

  public static CircuitBreaker fastFail() {
    return CircuitBreaker.of(
        "defaultCircuitBreaker",
        CircuitBreakerConfig
            .custom()

            .failureRateThreshold(21f)
            .minimumNumberOfCalls(1)
            .permittedNumberOfCallsInHalfOpenState(1)

            .waitDurationInOpenState(Duration.ofDays(365))
            .recordExceptions(CircuitCheckException.class)

            .build()
    );
  }
}
