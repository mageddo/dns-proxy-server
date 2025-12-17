package com.mageddo.wait;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mageddo.commons.concurrent.Threads;

import org.apache.commons.lang3.time.StopWatch;

import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true, fluent = true)
public class Wait<T> {

  @Setter
  private Duration timeout = Duration.ofSeconds(1);
  @Setter
  private Duration pollingEvery = Duration.ofMillis(1000 / 60);

  private final T obj;
  private Set<Class<? extends Throwable>> ignoredExceptions = new HashSet<>();
  private Throwable lastException = null;

  public Wait() {
    this.obj = null;
  }

  public Wait(T obj) {
    this.obj = obj;
  }

  public <R> R until(Supplier<R> sup) {
    return this.until((obj) -> sup.get());
  }

  public <R> R until(Function<T, R> fn) {
    final var stopWatch = StopWatch.createStarted();
    while (this.shouldContinue(stopWatch)) {
      try {
        final var result = fn.apply(this.obj);
        if (result != null) {
          return result;
        }
      } catch (final Exception e) {
        if (!this.ignoredExceptions.contains(e.getClass())) {
          throw e;
        } else {
          this.lastException = e;
        }
      }

      Threads.sleep(this.pollingEvery);
    }
    if (this.lastException != null) {
      throw new UnsatisfiedConditionException(this.lastException);
    }
    throw new UnsatisfiedConditionException();
  }

  private boolean shouldContinue(StopWatch stopWatch) {
    final var notInterrupted = !Thread.currentThread()
        .isInterrupted();
    return notInterrupted && stopWatch.getTime() < this.timeout.toMillis();
  }

  public Wait<T> ignoreException(Class<? extends Throwable> t) {
    this.ignoredExceptions.add(t);
    return this;
  }

  public Wait<T> infinityTimeout() {
    this.timeout = Duration.ofMillis(Long.MAX_VALUE);
    return this;
  }
}
