package com.mageddo.dnsproxyserver.server.rest.reqres;

import lombok.Value;

@Value
public class Message {
  private int code;
  private String message;

  public static Message of(int code, String msg){
    return new Message(code, msg);
  }
}
