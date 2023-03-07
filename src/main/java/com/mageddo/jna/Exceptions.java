package com.mageddo.jna;

import com.mageddo.commons.lang.ExecutionException;

public class Exceptions {
  public static ExecutionException doThrow(Number returnCode){
    return new ExecutionException(returnCode);
  }
}
