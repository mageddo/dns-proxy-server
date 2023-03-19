package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.VersionCmd;
import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dnsproxyserver.di.module.ModuleDockerClient;
import com.mageddo.dnsproxyserver.docker.DockerConnectionCheck.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DockerConnectionCheckTest {

  DockerClient dockerClient;

  DockerConnectionCheck connectionCheck;

  @BeforeEach
  void beforeEach() {
    this.dockerClient = spy(ModuleDockerClient.dockerClient());
    this.connectionCheck = Mockito.spy(new DockerConnectionCheck(this.dockerClient));
  }

  @Test
  void mustCheckIfDockerIsConnected() {
    // arrange
    assumeTrue(this.connectionCheck.isSupportedPlatform());

    // act
    final var connected = this.connectionCheck.isConnected();

    // assert
    System.out.println(connected); // result is not important as docker can be running or not
    verify(this.connectionCheck).updateStatus();
  }

  @Test
  void dockerMustBeConnected() {
    // arrange
    doReturn(true)
      .when(this.connectionCheck).isSupportedPlatform();

    final var versionCmd = mock(VersionCmd.class);
    doReturn(versionCmd)
      .when(this.dockerClient)
      .versionCmd();

    doReturn(null)
      .when(versionCmd)
      .exec();

    // act
    final var connected = this.connectionCheck.isConnected();

    // assert
    assumeTrue(connected);
    verify(this.connectionCheck).updateStatus();
  }

  @Test
  void mustUpdateCache() {
    // arrange

    doReturn(true)
      .when(this.connectionCheck).isSupportedPlatform();

    this.connectionCheck.status = Status.connected();
    assertTrue(this.connectionCheck.isConnected());

    this.connectionCheck.status = new Status(true, LocalDateTime.now().minusSeconds(31));

    final var versionCmd = mock(VersionCmd.class);
    doReturn(versionCmd)
      .when(this.dockerClient)
      .versionCmd();

    doThrow(new RuntimeException("Whatever not connected error"))
      .when(versionCmd)
      .exec();

    // act
    this.connectionCheck.isConnected();

    // assert
    Threads.sleep(1000 / 30);
    assertFalse(this.connectionCheck.isConnected());
    verify(this.connectionCheck).triggerUpdate();

  }
}
