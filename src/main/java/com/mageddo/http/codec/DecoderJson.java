package com.mageddo.http.codec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mageddo.json.JsonUtils;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;

public class DecoderJson implements Decoder {
  @Override
  public <T> T decode(HttpExchange exchange, Type t) {
    try {
      return JsonUtils
        .instance()
        .readValue(exchange.getRequestBody(), new TypeReference<T>() {
          @Override
          public Type getType() {
            return t;
          }
        });
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public <T> T decode(HttpExchange exchange, Class<T> t) {
    try {
      return JsonUtils
        .instance()
        .readValue(exchange.getRequestBody(), t);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
