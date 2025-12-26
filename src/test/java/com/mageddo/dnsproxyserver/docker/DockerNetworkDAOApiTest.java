package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;
import com.mageddo.dnsproxyserver.docker.dataprovider.DockerNetworkDAOApi;
import com.mageddo.http.HttpStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import testing.templates.docker.DockerClientTemplates;

import static com.mageddo.dnsproxyserver.docker.NetworkConnectionStatus.ALREADY_CONNECTED;
import static com.mageddo.dnsproxyserver.docker.NetworkConnectionStatus.CONNECTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class DockerNetworkDAOApiTest {

  DockerNetworkDAOApi dao;

  DockerClient dockerClient;

  @BeforeEach
  void before() {
    this.dockerClient = DockerClientTemplates.buildSpy();
    this.dao = new DockerNetworkDAOApi(this.dockerClient);
  }

  @Test
  void mustConnectContainerIsToNetwork() {
    // arrange
    final var netName = "dps";
    final var containerId = "a39bba9a8bab2899";

    // act
    final var status = this.dao.connect(netName, containerId);

    // assert
    assertEquals(CONNECTED, status);
  }

  @Test
  void mustNotThrowErrorWhenContainerIsAlreadyConnectedToNetwork() {
    // arrange
    final var netName = "dps";
    final var containerId = "a39bba9a8bab2899";

    final var connectToNetworkCmd = this.dockerClient.connectToNetworkCmd();
    doThrow(new DockerException("endpoint with name cobaia already exists in network dps",
        HttpStatus.FORBIDDEN
    ))
        .when(connectToNetworkCmd)
        .exec()
    ;

    // act
    final var status = this.dao.connect(netName, containerId);

    // assert
    assertEquals(ALREADY_CONNECTED, status);
  }

}
