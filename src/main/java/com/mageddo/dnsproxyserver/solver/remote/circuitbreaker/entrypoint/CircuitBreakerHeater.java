package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.entrypoint;

import java.util.List;

import javax.ejb.Singleton;
import javax.inject.Inject;

import com.mageddo.di.Eager;
import com.mageddo.dnsproxyserver.config.ConfigService;
import com.mageddo.dnsproxyserver.solver.remote.application.failsafe.CircuitBreakerFactory;
import com.mageddo.net.IpAddr;

import lombok.AllArgsConstructor;

@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class CircuitBreakerHeater implements Eager {

  private final CircuitBreakerFactory circuitBreakerFactory;
  private final ConfigService configService;

  @Override
  public void run() {
    this.findRemoteServers()
        .forEach(this.circuitBreakerFactory::findCircuitBreaker)
    ;
  }

  private List<IpAddr> findRemoteServers() {
    return this.configService.find()
        .getRemoteDnsServers();
  }
}
