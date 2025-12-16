package com.mageddo.dnsproxyserver.config;

import javax.ejb.Singleton;
import javax.inject.Inject;

import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAO;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigService {

  private final ConfigDAO configDAO;

  public Config find() {
    return this.configDAO.find();
  }

  public CircuitBreakerStrategyConfig findCircuitBreaker() {
    return this.find()
        .getSolverRemoteCircuitBreakerStrategy();
  }
}
