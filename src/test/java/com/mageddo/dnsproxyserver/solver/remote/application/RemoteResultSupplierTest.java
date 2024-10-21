package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.net.NetExecutorWatchdog;
import com.mageddo.utils.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.MessageTemplates;
import testing.templates.solver.remote.RequestTemplates;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoteResultSupplierTest {

  RemoteResultSupplier supplier;

  @BeforeEach
  void beforeEach() {
    final var req = RequestTemplates.buildDefault();
    final var netWatchdog = new NetExecutorWatchdog();
    final var executor = Executors.newThreadExecutor();
    this.supplier = Mockito.spy(new RemoteResultSupplier(req, executor, netWatchdog));
  }

  @Test
  void mustPingRemoteServerWhileQueryingWhenFeatureIsActive() {

    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var answer = MessageTemplates.buildAAnswer(query);

    doReturn(true)
      .when(this.supplier)
      .isPingWhileGettingQueryResponseActive();

    doReturn(CompletableFuture.completedFuture(answer))
      .when(this.supplier)
      .sendQueryAsyncToResolver(any());

    // act
    final var res = this.supplier.get();

    // assert
    assertNotNull(res);
    verify(this.supplier).pingWhileGettingQueryResponse(any(), any());

  }

  @Test
  void pingRemoteServerWhileQueryingDisabledByDefault(){

    // act
    final var active = this.supplier.isPingWhileGettingQueryResponseActive();

    // assert
    assertFalse(active);

  }
}
