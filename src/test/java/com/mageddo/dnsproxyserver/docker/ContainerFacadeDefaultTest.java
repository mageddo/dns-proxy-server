package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.mageddo.dnsproxyserver.docker.dataprovider.ContainerFacadeDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.docker.ContainerTemplates;
import testing.templates.docker.DockerClientTemplates;
import testing.templates.docker.InspectContainerResponseTemplates;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class ContainerFacadeDefaultTest {

  ContainerFacadeDefault dao;

  DockerClient dockerClient;

  @BeforeEach
  void before(){
    this.dockerClient = DockerClientTemplates.buildSpy();
    this.dao = spy(new ContainerFacadeDefault(this.dockerClient));
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
    final var inspectResponse = this.dao.inspect(containerId);

    // assert
    assertEquals(expected, inspectResponse);
  }

  @Test
  void mustNotThrowErrorWhenInspectContainerGetNotFound(){
    // arrange
    final var containerId = "a39bba9a8bab2899";

    doThrow(new NotFoundException("Container not found"))
      .when(this.dao).inspect(containerId)
    ;

    // act
    final var container = this.dao.safeInspect(containerId);

    // assert
    assertNull(container);
  }

  @Test
  void mustNotThrowErrorWhenInspectContainerFails(){
    // arrange
    final var containerId = "a39bba9a8bab28aa";

    doThrow(new NullPointerException("Unexpected failure"))
      .when(this.dao).inspect(containerId)
    ;

    // act
    final var container = this.dao.safeInspect(containerId);

    // assert
    assertNull(container);
  }

  @Test
  void mustFilterNullContainerInspections(){
    final var c1 = ContainerTemplates.buildRegularContainerCoffeeMakerCheckout();
    final var c2 = ContainerTemplates.buildDpsContainer();
    final var containers = List.of(c1, c2);

    doReturn(InspectContainerResponseTemplates.withDpsLabel())
      .when(this.dao).safeInspect(c1.getId())
    ;

    doReturn(null)
      .when(this.dao).safeInspect(c2.getId())
    ;

    final var filtered = this.dao.inspectFilteringValidContainers(containers)
      .toList();

    assertEquals(1, filtered.size());
  }
}
