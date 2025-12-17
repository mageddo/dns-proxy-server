package com.mageddo.http.codec;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.Response;

import com.sun.net.httpserver.HttpExchange;

public class Encoders {

  private Encoders() {
  }

  public static void encodeJson(HttpExchange exchange, Response.Status status, Object o) {
    encodeJson(exchange, status.getStatusCode(), o);
  }

  public static void encodeJson(HttpExchange exchange, int status, Object o) {
    new EncoderJson().encode(exchange, status, o);
  }

  public static void status(HttpExchange exchange, Response.Status status) {
    status(exchange, status.getStatusCode());
  }

  public static void status(HttpExchange exchange, final int statusCode) {
    try {
      exchange.sendResponseHeaders(statusCode, 0);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static void encodePlain(HttpExchange exchange, String text) {
    try {
      final var bytes = text.getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders()
          .add("Content-Type", "text/plain");
      exchange.sendResponseHeaders(200, bytes.length);
      exchange.getResponseBody()
          .write(bytes);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
