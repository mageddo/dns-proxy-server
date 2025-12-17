package testing.templates;

import java.time.Duration;

import com.mageddo.dnsproxyserver.config.CanaryRateThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.NonResilientCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.mapper.ConfigMapper;

public class CircuitBreakerConfigTemplates {

  public static StaticThresholdCircuitBreakerStrategyConfig buildDefault() {
    return ConfigMapper.staticThresholdCircuitBreakerConfig();
  }

  public static StaticThresholdCircuitBreakerStrategyConfig oneTryFailSuccess() {
    return StaticThresholdCircuitBreakerStrategyConfig
        .builder()
        .successThreshold(1)
        .failureThreshold(1)
        .failureThresholdCapacity(10)
        .testDelay(Duration.ofMillis(10))
        .build();
  }

  public static CircuitBreakerStrategyConfig buildNonResilientConfig() {
    return new NonResilientCircuitBreakerStrategyConfig();
  }

  public static CanaryRateThresholdCircuitBreakerStrategyConfig fastCanaryRateThreshold() {
    return CanaryRateThresholdCircuitBreakerStrategyConfig.builder()
        .permittedNumberOfCallsInHalfOpenState(10)
        .minimumNumberOfCalls(1)
        .failureRateThreshold(1)
        .build();
  }

}
