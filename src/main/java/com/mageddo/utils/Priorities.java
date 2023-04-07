package com.mageddo.utils;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Priorities {

  private Priorities() {
  }

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

  public static <T> Comparator<T> comparator(Function<T, String> fn, String ... keys) {
    return comparator(build(keys), fn);
  }

  public static <T> Comparator<T> comparator(Map<String, Integer> priorities, Function<T, String> fn) {
    return Comparator.comparing(it -> Priorities.compare(priorities, fn.apply(it)));
  }

}
