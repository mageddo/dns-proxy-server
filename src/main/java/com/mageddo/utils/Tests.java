package com.mageddo.utils;

public class Tests {

  private static final String JUNIT_FRAMEWORK_PACKAGE = "org.junit.";

  public static boolean inTest() {
    return hashJunitInStackTrace(Thread.currentThread());
  }

  private static boolean hashJunitInStackTrace(final Thread thread) {
    for (StackTraceElement element : thread.getStackTrace()) {
      if (element.getClassName().startsWith(JUNIT_FRAMEWORK_PACKAGE)) {
        return true;
      }
    }
    return false;
  }
}
