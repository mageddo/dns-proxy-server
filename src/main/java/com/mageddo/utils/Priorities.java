package com.mageddo.utils;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Priorities {
  public static Map<String, Integer> build(String... keys) {
    final var counter = new AtomicInteger();
    return Stream
      .of(keys)
      .map(it -> Pair.of(it, counter.getAndIncrement()))
      .collect(Collectors.toMap(Pair::getKey, Pair::getValue))
      ;
  }

  public static int compare(Map<String, Integer> priorities, String name) {
    Validate.isTrue(priorities.containsKey(name), "Key not found: %s", name);
    return priorities.get(name);
  }
}
