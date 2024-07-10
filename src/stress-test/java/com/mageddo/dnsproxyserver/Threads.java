package com.mageddo.dnsproxyserver;

import com.google.common.util.concurrent.UncheckedExecutionException;
import com.mageddo.commons.lang.exception.UnchekedInterruptedException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Threads {
  public static <T, R> List<R> submitAndWait(final List<T> items, final Function<T, Future<R>> mapper) {
    return flush(toFutures(items, mapper));
  }

  public static <T, R> List<Future<R>> toFutures(final List<T> items, final Function<T, Future<R>> mapper) {
    return items.stream()
      .map(mapper)
      .collect(Collectors.toList());
  }

  public static <T> List<T> flush(final List<Future<T>> futures) {
    final var results = new ArrayList<T>();
    for (final var future : futures) {
      try {
        results.add(future.get());
      } catch (InterruptedException e) {
        throw new UnchekedInterruptedException(e);
      } catch (ExecutionException e) {
        throw new UncheckedExecutionException(e);
      }
    }
    return results;
  }

}
