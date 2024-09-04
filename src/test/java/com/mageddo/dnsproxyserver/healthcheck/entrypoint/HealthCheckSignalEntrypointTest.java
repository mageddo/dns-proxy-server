package com.mageddo.dnsproxyserver.healthcheck.entrypoint;

import com.mageddo.dnsproxyserver.healthcheck.HealthCheck;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class HealthCheckSignalEntrypointTest {

  @Mock
  HealthCheck healthCheck;

  @Spy
  @InjectMocks
  HealthCheckSignalEntrypoint entrypoint;

  @Test
  void mustRegisterHandler() {
    this.entrypoint.registerHandler();
  }

  @Test
  void mustCatchFatalErrorsWhenRegisteringHandler() {

    doThrow(new AssertionError("mocked error"))
      .when(this.entrypoint)
      .registerHandler();

    this.entrypoint.safeRegisterHandler();

  }

}
