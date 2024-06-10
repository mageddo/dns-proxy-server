package com.mageddo.dnsproxyserver.config.validator;

import com.mageddo.dnsproxyserver.config.CircuitBreaker;
import org.apache.commons.lang3.Validate;

import static com.mageddo.dnsproxyserver.utils.Numbers.positiveOrNull;

public class CircuitBreakerValidator {
  public static void validate(CircuitBreaker circuit) {
    Validate.notNull(positiveOrNull(circuit.getFailureThreshold()), genMsg("failure theshold must be a positive number"));
    Validate.notNull(positiveOrNull(circuit.getSuccessThreshold()), genMsg("success theshold must be positive number"));
    Validate.notNull(positiveOrNull(circuit.getFailureThresholdCapacity()), genMsg("success theshold capacity must be positive number"));
    Validate.notNull(circuit.getTestDelay(), genMsg("test delay must be not null"));
  }

  private static String genMsg(String msg) {
    return "Circuit Breaker: " + msg;
  }
}
