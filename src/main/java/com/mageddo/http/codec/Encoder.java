package com.mageddo.http.codec;

import javax.ws.rs.core.Response;

import com.sun.net.httpserver.HttpExchange;

public interface Encoder {

  void encode(HttpExchange exchange, int status, Object o);

  default void encode(HttpExchange exchange, Response.Status status, Object o) {
    this.encode(exchange, status.getStatusCode(), o);
  }

}
