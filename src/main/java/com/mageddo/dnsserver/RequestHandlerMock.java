package com.mageddo.dnsserver;

import org.xbill.DNS.Message;

import java.util.ArrayList;
import java.util.List;

public class RequestHandlerMock implements RequestHandler {

  private List<Message> messages = new ArrayList<>();

  @Override
  public Message handle(Message query, String kind) {
    this.messages.add(query);
    return query;
  }

  public List<Message> getMessages() {
    return this.messages;
  }

  public Message getFirst() {
    return this.messages
      .stream()
      .findFirst()
      .orElse(null)
      ;
  }
}
