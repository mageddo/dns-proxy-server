package com.mageddo.jna;

public class Exceptions {
  public static ExecutionException doThrow(Number returnCode){
    return new ExecutionException(returnCode);
  }
}
