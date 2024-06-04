package com.mageddo.dnsproxyserver.solver.remote.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.CircuitBreakerConfigTemplates;
import testing.templates.InetSocketAddressTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerFactoryTest {

  @InjectMocks
  @Spy
  CircuitBreakerFactory factory;

  @Test
  void mustCheckAllExistentCircuitsAndCountSuccessWhenSafeCheckReturnsTrue() {
    // arrange
    doReturn(CircuitBreakerConfigTemplates.buildDefault())
      .when(this.factory)
      .findCircuitBreakerConfig()
    ;
    doReturn(true).when(this.factory).circuitBreakerSafeCheck(any());

    final var addr = InetSocketAddressTemplates._8_8_8_8();
    this.factory.createCircuitBreakerFor(addr);

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
    this.factory.createCircuitBreakerFor(addr);

    // act
    final var result = this.factory.checkCreatedCircuits();

    // assert
    assertEquals(0, result.getKey());
    assertEquals(1, result.getValue());
  }
}
