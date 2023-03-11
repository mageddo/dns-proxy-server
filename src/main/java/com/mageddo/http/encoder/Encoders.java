package com.mageddo.http.encoder;

import com.sun.net.httpserver.HttpExchange;

import javax.ws.rs.core.Response;

public class Encoders {

  private Encoders() {
  }

  public static void encodeJson(HttpExchange exchange, Response.Status status, Object o) {
    encodeJson(exchange, status.getStatusCode(), o);
  }

  public static void encodeJson(HttpExchange exchange, int status, Object o) {
    new EncoderJson().encode(exchange, status, o);
  }
}
