package com.mageddo.utils;

import java.util.concurrent.ExecutorService;

import com.mageddo.commons.concurrent.ThreadPool;

public class Executors {
  public static ExecutorService newThreadExecutor() {
    if (Boolean.getBoolean("mg.physical_threads.active")) {
      return ThreadPool.newFixed(50);
    }
    return java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor();
  }
}
