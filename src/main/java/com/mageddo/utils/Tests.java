package com.mageddo.utils;

import com.mageddo.commons.lang.Singletons;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class Tests {

  private static final String JUNIT_FRAMEWORK_PACKAGE = "org.junit.";
  private static final AtomicInteger hotCallsStat = new AtomicInteger();

  public static boolean inTest() {
    return Singletons.createOrGet(Tests.class, Tests::inTestHotLoad);
  }

  static boolean inTestHotLoad() {
    hotCallsStat.incrementAndGet();
    return findAllThreads()
      .stream()
      .anyMatch(Tests::hashJunitInStackTrace);
  }

  private static Set<Thread> findAllThreads(){
    return Thread.getAllStackTraces().keySet();
  };

  private static boolean hashJunitInStackTrace(final Thread thread) {
    return hashClassInStackTrace(
      thread,
      (element) -> element.getClassName().startsWith(JUNIT_FRAMEWORK_PACKAGE)
    );
  }

  private static boolean hashClassInStackTrace(Thread thread, final Predicate<StackTraceElement> p) {
    for (final var element : thread.getStackTrace()) {
      if (p.test(element)) {
        return true;
      }
    }
    return false;
  }

  static int getHotCallsStat() {
    return hotCallsStat.get();
  }

  static void resetStats(){
    hotCallsStat.set(0);
  }
}
