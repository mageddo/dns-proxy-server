package com.mageddo.http.codec;

import com.sun.net.httpserver.HttpExchange;

import java.lang.reflect.Type;

public interface Decoder {

  <T>T decode(HttpExchange exchange, Type t);

  <T>T decode(HttpExchange exchange, Class<T> t);

}
