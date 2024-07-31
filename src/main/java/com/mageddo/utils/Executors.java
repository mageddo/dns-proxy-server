package com.mageddo.utils;

import com.mageddo.commons.concurrent.ThreadPool;

import java.util.concurrent.ExecutorService;

public class Executors {
  public static ExecutorService newThreadExecutor() {
    if (Boolean.getBoolean("mg.physical_threads.active")) {
      return ThreadPool.newFixed(50);
    }
    return java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor();
  }
}
