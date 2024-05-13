package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerNetworkDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.server.dns.solver.docker.ContainerTemplates;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DpsContainerServiceTest {

  @Mock
  DockerNetworkDAO dockerNetworkDAO;

  @Spy
  @InjectMocks
  DpsContainerService dpsContainerService;

  @Test
  void mustConnectToDpsNetworkWhenNotConnectedYet() {

    // arrange
    final var container = ContainerTemplates.withDefaultBridgeNetworkOnly();

    // act
    this.dpsContainerService.connectDpsContainerToDpsNetwork(container);

    // assert
    verify(this.dockerNetworkDAO).connect(anyString(), anyString());
    verify(this.dpsContainerService, never()).fixDpsContainerIpAtDpsNetwork(any(), any());
  }

  @Test
  void mustFixContainerDpsNetworkIpWhenAlreadyConnectButIpIsWrong() {

    // arrange
    final var container = ContainerTemplates.withDpsLabel();

    // act
    this.dpsContainerService.connectDpsContainerToDpsNetwork(container);

    // assert
    verify(this.dockerNetworkDAO, never()).connect(anyString(), anyString());
    verify(this.dpsContainerService).fixDpsContainerIpAtDpsNetwork(any(), any());
  }

  @Test
  void mustDoNothingWhenItIsAlreadyCorrectlyConnectedToDpsNetwork() {

    // arrange
    final var container = ContainerTemplates.dpsContainer();

    // act
    this.dpsContainerService.connectDpsContainerToDpsNetwork(container);

    // assert
    verify(this.dockerNetworkDAO, never()).connect(anyString(), anyString());
    verify(this.dpsContainerService, never()).fixDpsContainerIpAtDpsNetwork(any(), any());
  }
}
