package testing.templates;

import com.mageddo.dnsproxyserver.config.CircuitBreaker;
import com.mageddo.dnsproxyserver.config.mapper.ConfigMapper;

import java.time.Duration;

public class CircuitBreakerConfigTemplates {
  public static CircuitBreaker buildDefault(){
    return ConfigMapper.defaultCircuitBreaker();
  }

  public static CircuitBreaker oneTryFailSuccess() {
    return CircuitBreaker
      .builder()
      .successThreshold(1)
      .failureThreshold(1)
      .failureThresholdCapacity(10)
      .testDelay(Duration.ofMillis(10))
      .build();
  }
}
