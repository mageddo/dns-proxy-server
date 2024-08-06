package com.mageddo.utils;

import java.util.function.Predicate;

public class Tests {

  private static final String JUNIT_FRAMEWORK_PACKAGE = "org.junit.";

  public static boolean inTest() {
    return hashJunitInStackTrace(Thread.currentThread());
  }

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
