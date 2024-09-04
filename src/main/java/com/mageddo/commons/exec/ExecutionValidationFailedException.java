package com.mageddo.commons.exec;

public class ExecutionValidationFailedException extends RuntimeException {
  private final Result result;

  public ExecutionValidationFailedException(Result result) {
    super(String.format("error, code=%d, error=%s", result.getExitCode(), result.getOutAsString()));
    this.result = result;
  }

  public Result result() {
    return this.result;
  }

  public int getExitCode() {
    return this.result.getExitCode();
  }
}
