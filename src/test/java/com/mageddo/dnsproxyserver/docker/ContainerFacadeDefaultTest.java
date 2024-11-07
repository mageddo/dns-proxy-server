package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.mageddo.dnsproxyserver.docker.dataprovider.ContainerFacadeDefault;
import testing.templates.docker.ContainerTemplates;
import testing.templates.docker.DockerClientTemplates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.docker.InspectContainerResponseTemplates;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ContainerFacadeDefaultTest {

  ContainerFacadeDefault dao;

  DockerClient dockerClient;

  @BeforeEach
  void before(){
    this.dockerClient = DockerClientTemplates.buildSpy();
    this.dao = new ContainerFacadeDefault(this.dockerClient);
  }

  @Test
  void mustFindContainerById(){
    // arrange
    final var mockReturn = new ArrayList<Container>();
    mockReturn.add(ContainerTemplates.buildDpsContainer());
    mockReturn.add(ContainerTemplates.buildRegularContainerCoffeeMakerCheckout());

    final var containerId = mockReturn.get(0).getId();

    final var inspectContainerCmd = this.dockerClient.listContainersCmd();
    doReturn(mockReturn)
      .when(inspectContainerCmd)
      .exec()
    ;

    // act
    final var container = this.dao.findById(containerId);

    // assert
    assertEquals(mockReturn.get(0), container);
  }

  @Test
  void mustReturnNullWhenFindContainerByIdNotFound(){
    // arrange
    final var mockReturn = new ArrayList<Container>();

    final var containerId = "abc123";

    final var listContainerCmd = this.dockerClient.listContainersCmd();
    doReturn(mockReturn)
      .when(listContainerCmd)
      .exec()
    ;

    // act
    final var container = this.dao.findById(containerId);

    // assert
    assertNull(container);
  }

  @Test
  void mustListActiveContainers(){
    // arrange
    final var expected = new ArrayList<Container>();
    expected.add(ContainerTemplates.buildRegularContainerCoffeeMakerCheckout());

    final var listContainerCmd = this.dockerClient.listContainersCmd();
    doReturn(expected)
      .when(listContainerCmd)
      .exec()
    ;

    // act
    final var findActiveContainersResponse = this.dao.findActiveContainers();

    // assert
    assertEquals(expected, findActiveContainersResponse);
  }

  @Test
  void mustInspectContainerById(){
    // arrange
    final var expected = InspectContainerResponseTemplates.ngixWithDefaultBridgeNetworkOnly();
    final var containerId = expected.getId();

    final var inspectContainerCmd = this.dockerClient.inspectContainerCmd(containerId);
    doReturn(expected)
      .when(inspectContainerCmd)
      .exec()
    ;

    // act
    final var inspectResponse = this.dao.inspect(containerId).orElseThrow();

    // assert
    assertEquals(expected, inspectResponse);
  }

  @Test
  void mustNotThrowErrorWhenInspectContainerNotFound(){
    // arrange
    final var containerId = "a39bba9a8bab2899";

    final var inspectContainerCmd = this.dockerClient.inspectContainerCmd(containerId);
    doThrow(new NotFoundException("Container not found"))
      .when(inspectContainerCmd)
      .exec()
    ;

    // act
    final var container = this.dao.inspect(containerId);

    // assert
    assertEquals(Optional.empty(), container);
  }

  @Test
  void mustNotThrowErrorWhenInspectContainerFails(){
    // arrange
    final var containerId = "a39bba9a8bab28aa";

    final var inspectContainerCmd = this.dockerClient.inspectContainerCmd(containerId);
    doThrow(new NullPointerException("Unexpected failure"))
      .when(inspectContainerCmd)
      .exec()
    ;

    // act
    final var container = this.dao.inspect(containerId);

    // assert
    assertEquals(Optional.empty(), container);
  }
}
