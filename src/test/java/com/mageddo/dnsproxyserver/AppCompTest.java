package com.mageddo.dnsproxyserver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    this.app = spy(new App(args));

    final var expectedException = this.mockExitMethod();

    // act
    final var exception = assertThrows(RuntimeException.class, () -> this.app.start());

    // assert
    assertEquals(expectedException.getMessage(), exception.getMessage());
    verify(this.app, never()).setupLogs();
    verify(this.app, never()).findConfig(any());
  }


  @Test
  void mustExitWhenVerrsionCmd() {
    // arrange
    final var args = new String[]{"--version"};
    this.app = spy(new App(args));

    final var expectedException = this.mockExitMethod();

    // act
    final var exception = assertThrows(RuntimeException.class, () -> this.app.start());

    // assert
    assertEquals(expectedException.getMessage(), exception.getMessage());
    verify(this.app, never()).setupLogs();
  }

  RuntimeException mockExitMethod() {
    final var expectedException = new RuntimeException("must exit");
    doThrow(expectedException)
      .when(this.app)
      .exitGracefully()
    ;
    return expectedException;
  }

}
