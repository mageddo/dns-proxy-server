package testing.templates;

import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategy;
import com.mageddo.dnsproxyserver.config.mapper.ConfigMapper;

import java.time.Duration;

public class CircuitBreakerConfigTemplates {
  public static StaticThresholdCircuitBreakerStrategy buildDefault(){
    return ConfigMapper.defaultCircuitBreaker();
  }

  public static StaticThresholdCircuitBreakerStrategy oneTryFailSuccess() {
    return StaticThresholdCircuitBreakerStrategy
      .builder()
      .successThreshold(1)
      .failureThreshold(1)
      .failureThresholdCapacity(10)
      .testDelay(Duration.ofMillis(10))
      .build();
  }
}
