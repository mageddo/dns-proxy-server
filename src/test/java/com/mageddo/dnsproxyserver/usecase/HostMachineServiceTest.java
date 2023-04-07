package com.mageddo.dnsproxyserver.usecase;

import com.mageddo.dnsproxyserver.docker.DockerDAO;
import testing.templates.IpTemplates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class HostMachineServiceTest {

  @Mock
  DockerDAO dockerDAO;

  @Spy
  @InjectMocks
  HostMachineService machineService;

  @Test
  void mustReturnHostIP() {
    // arrange
    doReturn(false)
      .when(this.machineService)
      .isDpsRunningInsideContainer()
    ;

    final var expectedIp = IpTemplates.local();
    doReturn(expectedIp)
      .when(this.machineService)
      .findCurrentMachineIp(any())
    ;

    // act
    final var ip = this.machineService.findHostMachineIP();

    // assert
    assertEquals(expectedIp, ip);
  }

  @Test
  void mustReturnHostIPEvenWhenRunningInsideDockerContainer() {
    // arrange
    doReturn(true)
      .when(this.machineService)
      .isDpsRunningInsideContainer()
    ;

    final var expectedIp = IpTemplates.local();
    doReturn(expectedIp)
      .when(this.dockerDAO)
      .findHostMachineIp(any())
    ;

    // act
    final var ip = this.machineService.findHostMachineIP();

    // assert
    assertEquals(expectedIp, ip);
  }
}
