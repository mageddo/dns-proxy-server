package com.mageddo.commons.exec;

import org.apache.commons.exec.CommandLine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandLinesTest {

  @Test
  void mustExecuteAndPrintOutputConcurrently() {

    final var result = CommandLines.exec(
      new CommandLine("bash")
        .addArgument("-c")
        .addArgument("echo hi && sleep 0.2 && echo hi2", false),
      new NopResultHandler()
    );

    result.watchOutputInDaemonThread();

    result.waitProcessToFinish();

    final var expectedOut = """
      hi
      hi2
      """;
    assertEquals(expectedOut, result.getOutAsString());
  }
}
