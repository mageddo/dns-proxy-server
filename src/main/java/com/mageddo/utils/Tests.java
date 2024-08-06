package com.mageddo.utils;

import com.mageddo.commons.lang.Singletons;

import java.util.Set;
import java.util.function.Predicate;

public class Tests {

  private static final String JUNIT_FRAMEWORK_PACKAGE = "org.junit.";

  public static boolean inTest() {
    return Singletons.createOrGet(Tests.class, Tests::inTestHotLoad);
  }

  private static boolean inTestHotLoad() {
    return findAllThreads()
      .stream()
      .anyMatch(Tests::hashJunitInStackTrace);
  }

  static Set<Thread> findAllThreads(){
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
}
