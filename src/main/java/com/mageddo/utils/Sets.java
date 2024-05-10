package com.mageddo.utils;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sets {

  public static <T> Set<T> ordered(T... o) {
    return Stream.of(o)
      .collect(Collectors.toCollection(LinkedHashSet::new))
      ;
  }
}
