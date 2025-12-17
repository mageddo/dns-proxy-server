package testing.templates.solver.remote;

import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegate;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegateNonResilient;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.HealthCheckerStatic;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold.CircuitBreakerDelegateSelfObservable;

public class CircuitBreakerDelegateTemplates {
  public static CircuitBreakerDelegate buildCanaryRateThreshold() {
    return new CircuitBreakerDelegateSelfObservable(
        new CircuitBreakerDelegateNonResilient(), new HealthCheckerStatic(true)
    );
  }
}
