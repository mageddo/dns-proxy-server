package com.mageddo.dnsproxyserver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AppCompTest {

  App app;

  @Test
  void mustExitWhenHelpCmd() {
    // arrange
    final var args = new String[]{"--help"};
    this.setupStub(args);

    final var expectedException = this.mockExitMethod();

    // act
    final var exception = assertThrows(RuntimeException.class, () -> this.app.start());

    // assert
    assertEquals(expectedException.getMessage(), exception.getMessage());
    verify(this.app, never()).setupLogs();
    verify(this.app, never()).findConfig(any());
  }

  @Test
  void mustExitWhenVersionCmd() {
    // arrange
    final var args = new String[]{"--version"};
    this.setupStub(args);

    final var expectedException = this.mockExitMethod();

    // act
    final var exception = assertThrows(RuntimeException.class, () -> this.app.start());

    // assert
    assertEquals(expectedException.getMessage(), exception.getMessage());
    verify(this.app, never()).setupLogs();
  }

  @Test
  void mustCreateTmpDirIfNotExists() {
    // arrange
    final var args = new String[]{"--create-tmp-dir"};
    this.setupStub(args);
    doNothing().when(this.app).startContext();

    // act
    this.app.start();

    // assert
    verify(this.app).createTmpDirIfNotExists();
    verify(this.app, never()).exitGracefully();
    verify(this.app).startContext();

  }

  @Test
  void mustHandleFatalErrors() {
    // arrange
    final var args = new String[]{"--create-tmp-dir"};
    this.setupStub(args);

    doThrow(new IllegalAccessError("mocked fatal error"))
      .when(this.app)
      .checkHiddenCommands()
    ;
    doNothing()
      .when(this.app)
      .exitWithError(anyInt())
    ;

    // act
    this.app.start();

    verify(this.app).exitWithError(anyInt());

  }

  RuntimeException mockExitMethod() {
    final var expectedException = new App.SystemExitException("testing");
    doThrow(expectedException)
      .when(this.app)
      .exitGracefully()
    ;
    return expectedException;
  }

  private void setupStub(String[] args) {
    this.app = spy(new App(args));
  }

}
