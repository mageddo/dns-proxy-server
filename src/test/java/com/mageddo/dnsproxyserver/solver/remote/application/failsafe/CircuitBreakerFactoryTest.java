package com.mageddo.dnsproxyserver.solver.remote.application.failsafe;

import com.mageddo.dnsproxyserver.solver.remote.application.FailsafeCircuitBreakerFactory;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegateNonResilient;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold.CircuitBreakerDelegateSelfObservable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.failsafe.CircuitBreaker;
import testing.templates.CircuitBreakerConfigTemplates;
import testing.templates.InetSocketAddressTemplates;
import testing.templates.solver.remote.CircuitBreakerDelegateTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerFactoryTest {

  @Spy
  @InjectMocks
  CircuitBreakerFactory factory;

  @Mock
  FailsafeCircuitBreakerFactory failsafeCircuitBreakerFactory;

  @Test
  void mustCreateANewCircuitBreakerInstanceWhenDifferentKeyIsUsed() {
    // arrange
    doReturn(CircuitBreakerConfigTemplates.buildDefault())
        .when(this.factory)
        .findCircuitBreakerConfig()
    ;

    doReturn(mock(CircuitBreaker.class))
        .when(this.failsafeCircuitBreakerFactory)
        .build(any(), any());

    // act
    final var a = this.factory.findCircuitBreaker(InetSocketAddressTemplates._8_8_8_8_addr());
    final var b = this.factory.findCircuitBreaker(InetSocketAddressTemplates._1_1_1_1_addr());

    // assert
    assertNotEquals(a, b);
    assertNotEquals(a.hashCode(), b.hashCode());
  }

  @Test
  void mustReuseCircuitBreakerInstanceWhenSameKeyIsUsed() {
    // arrange
    final var addr = InetSocketAddressTemplates._8_8_8_8_addr();

    doReturn(CircuitBreakerConfigTemplates.buildDefault())
        .when(this.factory)
        .findCircuitBreakerConfig()
    ;

    doReturn(mock(CircuitBreaker.class))
        .when(this.failsafeCircuitBreakerFactory)
        .build(any(), any());

    // act
    final var a = this.factory.findCircuitBreaker(addr);
    final var b = this.factory.findCircuitBreaker(addr);

    // assert
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    verify(this.factory, times(1)).findCircuitBreakerHotLoad(any());
  }

  @Test
  void mustCheckAllExistentCircuitsAndCountSuccessWhenSafeCheckReturnsTrue() {
    // arrange
    doReturn(CircuitBreakerConfigTemplates.buildDefault())
        .when(this.factory)
        .findCircuitBreakerConfig()
    ;
    doReturn(true).when(this.factory)
        .circuitBreakerSafeCheck(any());

    final var addr = InetSocketAddressTemplates._8_8_8_8_addr();
    this.factory.findCircuitBreaker(addr);

    // act
    final var result = this.factory.checkCreatedCircuits();

    // assert
    assertEquals(1, result.getKey());
    assertEquals(0, result.getValue());
  }


  @Test
  void mustCheckAndCountErrorWhenSafeCheckReturnsFalse() {
    // arrange
    doReturn(CircuitBreakerConfigTemplates.buildDefault())
        .when(this.factory)
        .findCircuitBreakerConfig()
    ;
    doReturn(false).when(this.factory)
        .circuitBreakerSafeCheck(any());

    final var addr = InetSocketAddressTemplates._8_8_8_8_addr();
    this.factory.findCircuitBreaker(addr);

    // act
    final var result = this.factory.checkCreatedCircuits();

    // assert
    assertEquals(0, result.getKey());
    assertEquals(1, result.getValue());
  }

  @Test
  void mustBuildNonResilientCircuitBreaker() {

    // arrange
    final var addr = InetSocketAddressTemplates._8_8_8_8_addr();
    doReturn(CircuitBreakerConfigTemplates.buildNonResilientConfig())
        .when(this.factory)
        .findCircuitBreakerConfig();

    // act
    final var circuitBreaker = this.factory.findCircuitBreaker(addr);

    // assert
    assertEquals(CircuitBreakerDelegateNonResilient.class, circuitBreaker.getClass());

  }

  @Test
  void mustReturnNullWhenNoStatusIsFound() {

    final var addr = InetSocketAddressTemplates._8_8_8_8();

    final var status = this.factory.findStatus(addr);

    assertNull(status);
  }

  @Test
  void mustBuildCanaryRateThresholdCircuitBreaker() {
    // arrange
    final var addr = InetSocketAddressTemplates._8_8_8_8();
    doReturn(CircuitBreakerConfigTemplates.fastCanaryRateThreshold())
        .when(this.factory)
        .findCircuitBreakerConfig();

    doReturn(CircuitBreakerDelegateTemplates.buildCanaryRateThreshold())
        .when(this.factory)
        .buildCanaryRateThreshold(any(), any());

    // act
    final var circuitBreaker = this.factory.findCircuitBreaker(addr);

    // assert
    assertEquals(CircuitBreakerDelegateSelfObservable.class, circuitBreaker.getClass());
  }

}
