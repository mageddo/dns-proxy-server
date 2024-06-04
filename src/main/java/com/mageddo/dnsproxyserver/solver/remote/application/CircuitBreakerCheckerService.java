package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dnsproxyserver.solver.SolverRemote;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.net.Networks;
import dev.failsafe.CircuitBreaker;
import dev.failsafe.CircuitBreakerOpenException;
import dev.failsafe.Failsafe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CircuitBreakerCheckerService {

  public boolean safeCheck(InetSocketAddress server, CircuitBreaker<Result> circuitBreaker) {
    try {
      this.check(server, circuitBreaker);
      return true;
    } catch (CircuitCheckException | CircuitBreakerOpenException e) {
      log.debug("status=serverNotHealth, server={}, msg={}, class={}", server, e.getMessage(), ClassUtils.getSimpleName(e));
    } catch (Exception e) {
      log.error("status=failedToCheckCircuit, server={}", server, e);
    }
    return false;
  }

  void check(InetSocketAddress server, CircuitBreaker<Result> circuitBreaker) {
    Failsafe
      .with(circuitBreaker)
      .run((ctx) -> {
        if (!this.ping(server)) {
          throw new CircuitCheckException("circuit breaker failed for " + server);
        }
        log.debug("status=serverIsHealthy, server={}", server);
      });
  }

  boolean ping(InetSocketAddress server) {
    return Networks.ping(server, SolverRemote.PING_TIMEOUT_IN_MS);
  }
}
