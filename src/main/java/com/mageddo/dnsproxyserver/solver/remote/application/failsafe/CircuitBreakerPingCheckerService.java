package com.mageddo.dnsproxyserver.solver.remote.application.failsafe;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dnsproxyserver.solver.remote.application.RemoteResultSupplier;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegate;
import com.mageddo.net.Networks;
import dev.failsafe.CircuitBreakerOpenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CircuitBreakerPingCheckerService {

  public boolean safeCheck(InetSocketAddress server, CircuitBreakerDelegate circuitBreaker) {
    try {
      this.check(server, circuitBreaker);
      return true;
    } catch (CircuitCheckException | CircuitBreakerOpenException e) { // fixme #533 excecao nao pode ser especifica
      log.debug("status=serverNotHealth, server={}, msg={}, class={}", server, e.getMessage(), ClassUtils.getSimpleName(e));
    } catch (Exception e) {
      log.error("status=failedToCheckCircuit, server={}", server, e);
    }
    return false;
  }

  void check(InetSocketAddress server, CircuitBreakerDelegate circuitBreaker) {
    circuitBreaker.execute(() -> {
      if (!this.ping(server)) {
        throw new CircuitCheckException("circuit breaker failed for " + server);
      }
      log.debug("status=serverIsHealthy, server={}", server);
      return null;
    });
  }

  /**
   * Note: Ping isn't being effective for DPS circuit breaker usage.
   *
   * @see https://github.com/mageddo/dns-proxy-server/issues/526#issuecomment-2261421618
   */
  boolean ping(InetSocketAddress server) {
    return Networks.ping(server, RemoteResultSupplier.PING_TIMEOUT_IN_MS);
  }
}
