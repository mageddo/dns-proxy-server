package com.mageddo.dnsproxyserver.solver.remote.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.InetSocketAddressTemplates;
import testing.templates.solver.remote.FailSafeCircuitBreakerTemplates;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerCheckerServiceTest {

  @Spy
  @InjectMocks
  CircuitBreakerCheckerService service;

  @Test
  void mustReturnTrueWhenPingGetSuccess() {
    // arrange
    final var addr = InetSocketAddressTemplates._8_8_8_8();
    final var circuitBreaker = FailSafeCircuitBreakerTemplates.buildDefault();
    doReturn(true).when(this.service).ping(any());

    // act
    final var ok = this.service.safeCheck(addr, circuitBreaker);

    // assert
    assertTrue(ok);
  }

  @Test
  void mustReturnFalseWhenPingReturnsFalse() {
    // arrange
    final var addr = InetSocketAddressTemplates._8_8_8_8();
    final var circuitBreaker = FailSafeCircuitBreakerTemplates.buildDefault();
    doReturn(false).when(this.service).ping(any());

    // act
    final var ok = this.service.safeCheck(addr, circuitBreaker);

    // assert
    assertFalse(ok);
  }

  @Test
  void mustReturnFalseWhenThereIsAFatalException() {
    // arrange
    final var addr = InetSocketAddressTemplates._8_8_8_8();
    final var circuitBreaker = FailSafeCircuitBreakerTemplates.buildDefault();
    doThrow(new RuntimeException("unknown error")).when(this.service).ping(any());

    // act
    final var ok = this.service.safeCheck(addr, circuitBreaker);

    // assert
    assertFalse(ok);
  }

}
