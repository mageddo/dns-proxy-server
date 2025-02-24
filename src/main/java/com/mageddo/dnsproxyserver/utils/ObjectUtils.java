package com.mageddo.dnsproxyserver.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ObjectUtils {

  public static String firstNonBlankRequiring(String... args) {
    return Optional
      .ofNullable(StringUtils.firstNonBlank(args))
      .orElseThrow(throwError())
      ;
  }

  public static <T> List<T> firstNonEmptyListRequiring(List<List<T>> lists) {
    for (final var list : lists) {
      if (!list.isEmpty()) {
        return list;
      }
    }
    throw throwError().get();
  }

  public static <T> T firstNonNull(List<T> args) {
    return (T) org.apache.commons.lang3.ObjectUtils.firstNonNull(args.toArray(Object[]::new));
  }

  public static <T> T firstNonNullRequiring(List<T> args) {
    return (T) firstNonNullRequiring(args.toArray(Object[]::new));
  }

  public static <T> T firstNonNullRequiring(T... args) {
    return Optional
      .ofNullable(org.apache.commons.lang3.ObjectUtils.firstNonNull(args))
      .orElseThrow(throwError())
      ;
  }

  public static <T> T firstMatchRequiring(List<T> args, Predicate<T> predicate) {
    return args
      .stream()
      .filter(predicate)
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("At least one argument should match the predicate!"))
      ;
  }

  static Supplier<IllegalArgumentException> throwError() {
    return () -> new IllegalArgumentException("At least one argument shouldn't be null!");
  }

}
