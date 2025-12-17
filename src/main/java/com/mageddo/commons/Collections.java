package com.mageddo.commons;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Collections {
  public static <T, R> Map<R, T> keyBy(Collection<T> c, Function<T, R> fn) {
    if (c == null) {
      return null;
    }
    if (c.isEmpty()) {
      return new HashMap<>();
    }
    return c.stream()
        .collect(Collectors.toMap(fn, Function.identity()));
  }
}
