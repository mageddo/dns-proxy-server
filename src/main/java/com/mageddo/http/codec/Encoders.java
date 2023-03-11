package com.mageddo.http.codec;

import com.sun.net.httpserver.HttpExchange;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.UncheckedIOException;

public class Encoders {

  private Encoders() {
  }

  public static void encodeJson(HttpExchange exchange, Response.Status status, Object o) {
    encodeJson(exchange, status.getStatusCode(), o);
  }

  public static void encodeJson(HttpExchange exchange, int status, Object o) {
    new EncoderJson().encode(exchange, status, o);
  }

  public static void noBody(HttpExchange exchange, Response.Status status) {
    noBody(exchange, status.getStatusCode());
  }

  public static void noBody(HttpExchange exchange, final int statusCode) {
    try {
      exchange.sendResponseHeaders(statusCode, 0);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
