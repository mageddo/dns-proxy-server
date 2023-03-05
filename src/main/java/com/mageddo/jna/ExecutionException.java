package com.mageddo.jna;

public class ExecutionException extends RuntimeException {

  private final Number returnCode;

  public ExecutionException(Number returnCode) {
    super(new RuntimeException(String.format("%d", returnCode)));
    this.returnCode = returnCode;
  }

  public int getCode() {
    return this.returnCode.intValue();
  }
}
