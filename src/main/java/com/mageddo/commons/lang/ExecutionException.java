package com.mageddo.commons.lang;

public class ExecutionException extends RuntimeException {

  private final Number returnCode;

  public ExecutionException(Number returnCode) {
    super(new RuntimeException(
        String.format("Execution failed, return code: int=%d, hex=%x", returnCode, returnCode)));
    this.returnCode = returnCode;
  }

  public int getCode() {
    return this.returnCode.intValue();
  }
}
