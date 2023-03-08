package com.mageddo.jna;

import com.mageddo.commons.lang.ExecutionException;
import com.sun.jna.LastErrorException;

public class Exceptions {
  public static ExecutionException doThrow(Number returnCode) {
    return new ExecutionException(returnCode);
  }

  public static boolean is(final LastErrorException e, final int code) {
    return e.getErrorCode() == code;
  }
}
