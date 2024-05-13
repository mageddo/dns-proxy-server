package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DpsContainerDAODefault.DPS_INSIDE_CONTAINER;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class DpsDockerEnvironmentSetupServiceTest {

  @Spy
  @InjectMocks
  DpsDockerEnvironmentSetupService dpsDockerEnvironmentSetupService;

  @Test
  void mustCheckIsRunningInsideContainer() {
    // arrange
    doReturn(DPS_INSIDE_CONTAINER)
      .when(this.dpsDockerEnvironmentSetupService).dpsContainerDAO.getDpsContainerEnv()
    ;

    // act
    final var insideContainer = this.dpsDockerEnvironmentSetupService.dpsContainerDAO.isDpsRunningInsideContainer(this.dpsDockerEnvironmentSetupService);

    // assert
    assertTrue(insideContainer);
  }
}
