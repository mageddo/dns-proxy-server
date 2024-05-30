package com.mageddo.dnsproxyserver.solver.docker.application;

import com.mageddo.dnsproxyserver.solver.docker.application.DpsContainerService;
import com.mageddo.dnsproxyserver.solver.docker.application.DpsDockerEnvironmentSetupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DpsDockerEnvironmentSetupServiceTest {

  @Mock
  DpsContainerService dpsContainerService;

  @Spy
  @InjectMocks
  DpsDockerEnvironmentSetupService setupService;

  @Test
  void mustSetupNetworkWhenFeatureIsActive(){

    // arrange
    doReturn(true)
      .when(this.setupService)
      .isMustConfigureDpsNetwork()
    ;
    doNothing()
      .when(this.setupService)
      .createNetworkIfAbsent()
    ;

    // act
    this.setupService.setupNetwork();

    // assert
    verify(this.setupService).createNetworkIfAbsent();
    verify(this.dpsContainerService).connectDpsContainerToDpsNetwork();

  }

  @Test
  void mustDoNothingSetupNetworkWhenFeatureIsInactive(){

    // arrange
    doReturn(false)
      .when(this.setupService)
      .isMustConfigureDpsNetwork()
    ;

    // act
    this.setupService.setupNetwork();

    // assert
    verify(this.setupService, never()).createNetworkIfAbsent();
    verify(this.dpsContainerService, never()).connectDpsContainerToDpsNetwork();

  }

}
