package com.mageddo.http.codec;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.mageddo.json.JsonUtils;
import com.sun.net.httpserver.HttpExchange;

public class EncoderJson implements Encoder {

  @Override
  public void encode(HttpExchange exchange, int status, Object o) {
    try {
      final var jsonBytes = JsonUtils
          .instance()
          .writeValueAsBytes(o);

      exchange
          .getResponseHeaders()
          .set("Content-Type", "application/json");
      ;
      exchange.sendResponseHeaders(status, jsonBytes.length);
      exchange
          .getResponseBody()
          .write(jsonBytes)
      ;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

  }
}
