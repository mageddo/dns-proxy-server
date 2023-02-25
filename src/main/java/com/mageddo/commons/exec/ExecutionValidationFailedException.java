package com.mageddo.commons.exec;

public class ExecutionValidationFailedException extends RuntimeException {
  private final CommandLines.Result result;

  public ExecutionValidationFailedException(CommandLines.Result result) {
    super(String.format("error, code=%d, error=%s", result.getExitCode(), result.getOutAsString()));
    this.result = result;
  }

  public CommandLines.Result result() {
    return this.result;
  }
}
