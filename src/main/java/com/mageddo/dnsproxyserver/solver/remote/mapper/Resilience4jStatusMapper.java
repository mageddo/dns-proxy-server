package com.mageddo.dnsproxyserver.solver.remote.mapper;

import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;

import org.apache.commons.lang3.EnumUtils;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

public class Resilience4jStatusMapper {
  public static CircuitStatus toCircuitStatus(CircuitBreaker.State state) {
    return EnumUtils.getEnum(CircuitStatus.class, state.name());
  }
}
