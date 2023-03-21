package com.mageddo.dnsproxyserver.docker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.mageddo.dnsproxyserver.docker.DpsContainerManager.DPS_INSIDE_CONTAINER;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class DpsContainerManagerTest {

  @Spy
  @InjectMocks
  DpsContainerManager dpsContainerManager;

  @Test
  void mustCheckIsRunningInsideContainer() {
    // arrange
    doReturn(DPS_INSIDE_CONTAINER)
      .when(this.dpsContainerManager)
      .getDpsContainerEnv()
    ;

    // act
    final var insideContainer = this.dpsContainerManager.isDpsRunningInsideContainer();

    // assert
    assertTrue(insideContainer);
  }
}
