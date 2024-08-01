package com.mageddo.dnsproxyserver.solver.remote.application.failsafe;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.CircuitBreakerConfigTemplates;
import testing.templates.InetSocketAddressTemplates;
import testing.templates.solver.remote.ResultSupplierTemplates;

import java.net.InetSocketAddress;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    this.factory.createOrGetCircuitBreaker(addr);

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
    this.factory.createOrGetCircuitBreaker(addr);

    // act
    final var result = this.factory.checkCreatedCircuits();

    // assert
    assertEquals(0, result.getKey());
    assertEquals(1, result.getValue());
  }

  @Test
  void mustNotFlushCacheWhenChangeStateToHalfOpen(){

    // arrange
    assertEquals("[]", this.factory.stats().toString());

    final var addr = InetSocketAddressTemplates._8_8_8_8();
    final var supError = ResultSupplierTemplates.alwaysFail();
    final var supSuccess = ResultSupplierTemplates.alwaysSuccess();

    doReturn(CircuitBreakerConfigTemplates.oneTryFailSuccess())
      .when(this.factory)
      .findCircuitBreakerConfig()
    ;

    this.checkFailAndSleep(addr, supError);
    this.checkFailAndSleep(addr, supError);

    this.factory.check(addr, supSuccess);
    assertEquals(
      "[CircuitBreakerFactory.Stats(remoteServerAddress=/8.8.8.8:53, state=CLOSED)]",
      this.factory.stats().toString()
    );
    verify(this.factory, times(2)).flushCache();


  }

  void checkFailAndSleep(InetSocketAddress addr, Supplier<Result> supError) {
    assertThrows(CircuitCheckException.class, () -> this.factory.check(addr, supError));
    assertEquals(
      "[CircuitBreakerFactory.Stats(remoteServerAddress=/8.8.8.8:53, state=OPEN)]",
      this.factory.stats().toString()
    );
    verify(this.factory).flushCache();
    Threads.sleep(100);
  }
}
