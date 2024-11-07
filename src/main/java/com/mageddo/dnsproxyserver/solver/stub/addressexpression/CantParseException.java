package com.mageddo.dnsproxyserver.solver.stub.addressexpression;

public class CantParseException extends RuntimeException {

  public CantParseException(String message) {
    super(message);
  }

  public CantParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
