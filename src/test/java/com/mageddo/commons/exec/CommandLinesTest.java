package com.mageddo.commons.exec;

import org.apache.commons.exec.CommandLine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandLinesTest {


  @Test
  void mustValidateWhenExitsWithErrorCode(){

    final var result = CommandLines.exec(
      new CommandLine("sh")
      .addArgument("-c")
      .addArgument("exit 3", false)
    );

    final var ex = assertThrows(ExecutionValidationFailedException.class, result::checkExecution);

    assertEquals(3, ex.getExitCode());

  }

  @Test
  void mustExecuteCommand(){

    final var result = CommandLines.exec("echo %s", "hey");

    assertEquals(0, result.getExitCode());
    assertEquals("hey\n", result.getOutAsString());
  }

  @Test
  void mustExecuteAndPrintOutputConcurrently() {

    final var result = CommandLines.exec(
      new CommandLine("sh")
        .addArgument("-c")
        .addArgument("echo hi && sleep 0.2 && echo hi2", false),
      new NopResultHandler()
    );

    result.printOutToLogsInBackground();

    result.waitProcessToFinish();

    final var expectedOut = """
      hi
      hi2
      """;
    assertEquals(expectedOut, result.getOutAsString());
  }
}
