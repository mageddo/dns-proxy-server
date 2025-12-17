package com.mageddo.dnsproxyserver.solver.docker.dataprovider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.mageddo.dnsproxyserver.solver.docker.dataprovider.DpsContainerDAODefault.DPS_INSIDE_CONTAINER_YES;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class DpsContainerDAODefaultTest {

  @Spy
  @InjectMocks
  DpsContainerDAODefault dpsContainerDAO;

  @Test
  void mustCheckIsRunningInsideContainer() {
    // arrange
    doReturn(DPS_INSIDE_CONTAINER_YES)
        .when(this.dpsContainerDAO)
        .getDpsContainerEnvValue()
    ;

    // act
    final var insideContainer = this.dpsContainerDAO.isDpsRunningInsideContainer();

    // assert
    assertTrue(insideContainer);
  }

}
