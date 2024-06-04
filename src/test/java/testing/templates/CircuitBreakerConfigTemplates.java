package testing.templates;

import com.mageddo.dnsproxyserver.config.CircuitBreaker;
import com.mageddo.dnsproxyserver.config.mapper.ConfigMapper;

public class CircuitBreakerConfigTemplates {
  public static CircuitBreaker buildDefault(){
    return ConfigMapper.defaultCircuitBreaker();
  }
}
