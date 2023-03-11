package com.mageddo.http.codec;

import com.sun.net.httpserver.HttpExchange;

public class Decoders {

  private Decoders() {
  }

  public static  <T> T jsonDecode(HttpExchange exchange, Class<T> clazz) {
    return new DecoderJson().decode(exchange, clazz);
  }
}
