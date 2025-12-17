package com.mageddo.http;

import java.util.List;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;

import org.apache.hc.core5.http.NameValuePair;

public class Request {
  public static List<String> queryParams(HttpExchange exchange, String param) {
    return UriUtils.findQueryParams(exchange.getRequestURI())
        .stream()
        .filter(it -> it.getName()
            .equals(param)) // https://stackoverflow.com/a/24700171/2979435
        .map(NameValuePair::getValue)
        .collect(Collectors.toList())
        ;
  }

  /**
   * @return the first query param.
   */
  public static String queryParam(HttpExchange exchange, String param) {
    return queryParams(exchange, param)
        .stream()
        .findFirst()
        .orElse(null)
        ;
  }
}
