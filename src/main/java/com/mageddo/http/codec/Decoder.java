package com.mageddo.http.codec;

import java.lang.reflect.Type;

import com.sun.net.httpserver.HttpExchange;

public interface Decoder {

  <T> T decode(HttpExchange exchange, Type t);

  <T> T decode(HttpExchange exchange, Class<T> t);

}
