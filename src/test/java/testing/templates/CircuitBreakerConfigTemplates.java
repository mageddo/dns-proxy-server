package testing.templates;

import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.NonResilientCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.mapper.ConfigMapper;

import java.time.Duration;

public class CircuitBreakerConfigTemplates {

  public static StaticThresholdCircuitBreakerStrategyConfig buildDefault(){
    return ConfigMapper.defaultCircuitBreaker();
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
}
