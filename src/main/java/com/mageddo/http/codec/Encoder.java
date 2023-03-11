package com.mageddo.http.codec;

import com.sun.net.httpserver.HttpExchange;

import javax.ws.rs.core.Response;

public interface Encoder {

  void encode(HttpExchange exchange, int status, Object o);

  default void encode(HttpExchange exchange, Response.Status status, Object o) {
    this.encode(exchange, status.getStatusCode(), o);
  }

}
