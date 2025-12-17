package com.mageddo.commons.lang;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Function;

public final class ValueResolver {

  private ValueResolver() {
  }

  public static <T, R1> R1 findFirst(
      Collection<T> list,
      Function<T, R1> f1
  ) {
    return findFirstInternal(list, f1);
  }

  public static <T, R1, R2> R2 findFirst(
      Collection<T> list,
      Function<T, R1> f1,
      Function<R1, R2> f2
  ) {
    return findFirstInternal(list, f1, f2);
  }

  public static <T, R1, R2, R3> R3 findFirst(
      Collection<T> list,
      Function<T, R1> f1,
      Function<R1, R2> f2,
      Function<R2, R3> f3
  ) {
    return findFirstInternal(list, f1, f2, f3);
  }

  public static <T, R1, R2, R3, R4> R4 findFirst(
      Collection<T> list,
      Function<T, R1> f1,
      Function<R1, R2> f2,
      Function<R2, R3> f3,
      Function<R3, R4> f4
  ) {
    return findFirstInternal(list, f1, f2, f3, f4);
  }

  public static <T, R1, R2, R3, R4, R5> R5 findFirst(
      Collection<T> list,
      Function<T, R1> f1,
      Function<R1, R2> f2,
      Function<R2, R3> f3,
      Function<R3, R4> f4,
      Function<R4, R5> f5
  ) {
    return findFirstInternal(list, f1, f2, f3, f4, f5);
  }

  /* =========================================================
   * Public API — findFirstOrThrow (type-safe overloads)
   * ========================================================= */

  public static <T, R1> R1 findFirstOrThrow(
      Collection<T> list,
      Function<T, R1> f1
  ) {
    return orThrow(findFirst(list, f1));
  }

  public static <T, R1, R2> R2 findFirstOrThrow(
      Collection<T> list,
      Function<T, R1> f1,
      Function<R1, R2> f2
  ) {
    return orThrow(findFirst(list, f1, f2));
  }

  public static <T, R1, R2, R3> R3 findFirstOrThrow(
      Collection<T> list,
      Function<T, R1> f1,
      Function<R1, R2> f2,
      Function<R2, R3> f3
  ) {
    return orThrow(findFirst(list, f1, f2, f3));
  }

  public static <T, R1, R2, R3, R4> R4 findFirstOrThrow(
      Collection<T> list,
      Function<T, R1> f1,
      Function<R1, R2> f2,
      Function<R2, R3> f3,
      Function<R3, R4> f4
  ) {
    return orThrow(findFirst(list, f1, f2, f3, f4));
  }

  public static <T, R1, R2, R3, R4, R5> R5 findFirstOrThrow(
      Collection<T> list,
      Function<T, R1> f1,
      Function<R1, R2> f2,
      Function<R2, R3> f3,
      Function<R3, R4> f4,
      Function<R4, R5> f5
  ) {
    return orThrow(findFirst(list, f1, f2, f3, f4, f5));
  }

  /* =========================================================
   * Internal implementation
   * ========================================================= */

  @SafeVarargs
  @SuppressWarnings("unchecked")
  private static <T, R> R findFirstInternal(
      Collection<T> list,
      Function<?, ?>... mappers
  ) {

    if (list == null || list.isEmpty() || mappers == null || mappers.length == 0) {
      return null;
    }

    for (final var element : list) {
      if (element == null) {
        continue;
      }

      var current = (Object) element;
      var valid = true;

      for (final var mapper : mappers) {
        if (current == null) {
          valid = false;
          break;
        }

        final var fn = (Function<Object, Object>) mapper;
        current = fn.apply(current);

        if (current == null) {
          valid = false;
          break;
        }
      }

      if (valid) {
        return (R) current;
      }
    }

    return null;
  }

  private static <R> R orThrow(R value) {
    if (value == null) {
      throw new NoSuchElementException();
    }
    return value;
  }
}
