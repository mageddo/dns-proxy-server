package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegate;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegateStaticThresholdFailsafe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.CircuitBreakerConfigTemplates;
import testing.templates.InetSocketAddressTemplates;
import testing.templates.solver.remote.ResultSupplierTemplates;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FailsafeCircuitBreakerFactoryTest {

  @Mock
  OnCacheMustBeFlushedEvent onCacheMustBeFlushedEvent;

  @Spy
  @InjectMocks
  FailsafeCircuitBreakerFactory factory;

  @Test
  void mustNotFlushCacheWhenChangeStateToHalfOpen() {

    // arrange
    final var addr = InetSocketAddressTemplates._8_8_8_8();
    final var config = CircuitBreakerConfigTemplates.oneTryFailSuccess();

    final var supError = ResultSupplierTemplates.alwaysFail();
    final var supSuccess = ResultSupplierTemplates.alwaysSuccess();

    final var circuitBreaker = new CircuitBreakerDelegateStaticThresholdFailsafe(this.factory.build(addr, config));
    assertEquals("CLOSED", circuitBreaker.findStatus().toString());

    // act
    this.checkFailAndSleep(circuitBreaker, supError);
    this.checkFailAndSleep(circuitBreaker, supError);

    circuitBreaker.execute(supSuccess);

    // assert
    assertEquals(
      "CLOSED",
      circuitBreaker.findStatus().toString()
    );

    verify(this.onCacheMustBeFlushedEvent, times(2)).run();

  }

  void checkFailAndSleep(CircuitBreakerDelegate circuitBreaker, Supplier<Result> supError) {
    assertThrows(CircuitCheckException.class, () -> circuitBreaker.execute(supError));
    assertEquals(
      "OPEN",
      circuitBreaker.findStatus().toString()
    );
    verify(this.onCacheMustBeFlushedEvent).run();
    Threads.sleep(100);
  }
}
