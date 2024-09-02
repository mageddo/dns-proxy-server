package com.mageddo.dnsproxyserver.solver.remote.application.failsafe;

import com.mageddo.dnsproxyserver.solver.remote.application.FailsafeCircuitBreakerFactory;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegateNonResilient;
import dev.failsafe.CircuitBreaker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.CircuitBreakerConfigTemplates;
import testing.templates.InetSocketAddressTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerFactoryTest {

  @InjectMocks
  @Spy
  CircuitBreakerFactory factory;

  @Mock
  FailsafeCircuitBreakerFactory failsafeCircuitBreakerFactory;

  @Test
  void mustCreateANewCircuitBreakerInstanceWhenDifferentKeyIsUsed(){
    // arrange
    doReturn(CircuitBreakerConfigTemplates.buildDefault())
      .when(this.factory)
      .findCircuitBreakerConfig()
    ;

    doReturn(mock(CircuitBreaker.class))
      .when(this.failsafeCircuitBreakerFactory)
      .build(any(), any());

    // act
    final var a = this.factory.findCircuitBreaker(InetSocketAddressTemplates._8_8_8_8());
    final var b = this.factory.findCircuitBreaker(InetSocketAddressTemplates._1_1_1_1());

    // assert
    assertNotEquals(a, b);
    assertNotEquals(a.hashCode(), b.hashCode());
  }

  @Test
  void mustReuseCircuitBreakerInstanceWhenSameKeyIsUsed(){
    // arrange
    final var addr = InetSocketAddressTemplates._8_8_8_8();

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
  }

  @Test
  void mustCheckAllExistentCircuitsAndCountSuccessWhenSafeCheckReturnsTrue() {
    // arrange
    doReturn(CircuitBreakerConfigTemplates.buildDefault())
      .when(this.factory)
      .findCircuitBreakerConfig()
    ;
    doReturn(true).when(this.factory).circuitBreakerSafeCheck(any());

    final var addr = InetSocketAddressTemplates._8_8_8_8();
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
    doReturn(false).when(this.factory).circuitBreakerSafeCheck(any());

    final var addr = InetSocketAddressTemplates._8_8_8_8();
    this.factory.findCircuitBreaker(addr);

    // act
    final var result = this.factory.checkCreatedCircuits();

    // assert
    assertEquals(0, result.getKey());
    assertEquals(1, result.getValue());
  }

  @Test
  void mustBuildNonResilientCircuitBreaker(){

    // arrange
    final var addr = InetSocketAddressTemplates._8_8_8_8();
    doReturn(CircuitBreakerConfigTemplates.buildNonResilientConfig())
      .when(this.factory)
      .findCircuitBreakerConfig();

    // act
    final var circuitBreaker = this.factory.findCircuitBreaker(addr);

    // assert
    assertEquals(CircuitBreakerDelegateNonResilient.class, circuitBreaker.getClass());

  }

  @Test
  void mustReturnNullWhenNoStatusIsFound(){

    final var addr = InetSocketAddressTemplates._8_8_8_8();

    final var status = this.factory.findStatus(addr);

    assertNull(status);
  }

}
