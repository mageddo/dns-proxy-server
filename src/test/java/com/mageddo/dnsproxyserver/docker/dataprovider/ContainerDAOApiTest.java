package com.mageddo.dnsproxyserver.docker.dataprovider;

import java.util.List;

import com.github.dockerjava.api.exception.NotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import testing.templates.docker.ContainerTemplates;
import testing.templates.docker.InspectContainerResponseTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ContainerDAOApiTest {

  @Spy
  @InjectMocks
  ContainerDAOApi facade;

  @Test
  void mustNotThrowErrorWhenInspectContainerGetNotFound() {
    // arrange
    final var containerId = "a39bba9a8bab2899";

    doThrow(new NotFoundException("Container not found"))
        .when(this.facade)
        .inspect(containerId)
    ;

    // act
    final var container = this.facade.safeInspect(containerId);

    // assert
    assertNull(container);
  }

  @Test
  void mustNotThrowErrorWhenInspectContainerFails() {
    // arrange
    final var containerId = "a39bba9a8bab28aa";

    doThrow(new NullPointerException("Unexpected failure"))
        .when(this.facade)
        .inspect(containerId)
    ;

    // act
    final var container = this.facade.safeInspect(containerId);

    // assert
    assertNull(container);
  }

  @Test
  void mustFilterNullContainerInspections() {
    final var c1 = ContainerTemplates.buildRegularContainerCoffeeMakerCheckout();
    final var c2 = ContainerTemplates.buildDpsContainer();
    final var containers = List.of(c1, c2);

    doReturn(InspectContainerResponseTemplates.withDpsLabel())
        .when(this.facade)
        .safeInspect(c1.getId())
    ;

    doReturn(null)
        .when(this.facade)
        .safeInspect(c2.getId())
    ;

    final var filtered = this.facade.inspectFilteringValidContainers(containers)
        .toList();

    assertEquals(1, filtered.size());
  }
}
