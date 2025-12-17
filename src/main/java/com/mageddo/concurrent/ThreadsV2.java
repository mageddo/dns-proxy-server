package com.mageddo.concurrent;

public class ThreadsV2 {
  public static boolean isInterrupted() {
    return Thread.currentThread()
        .isInterrupted();
  }

  public static boolean isNotInterrupted() {
    return !isInterrupted();
  }
}
