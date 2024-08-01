package com.mageddo.concurrent;

import com.mageddo.commons.concurrent.Threads;
import com.mageddo.commons.lang.exception.UnchekedInterruptedException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class SingleThreadQueueProcessor implements AutoCloseable {

  private final BlockingQueue<Runnable> queue;
  private final ExecutorService executor;

  public SingleThreadQueueProcessor() {
    this(new LinkedBlockingQueue<>());
  }

  public SingleThreadQueueProcessor(BlockingQueue<Runnable> queue) {
    this.queue = queue;
    this.executor = Executors.newSingleThreadExecutor(this::buildThread);
    this.startConsumer();
  }

  public void schedule(Runnable task) {
    try {
      this.queue.put(task);
    } catch (InterruptedException e) {
      throw new UnchekedInterruptedException(e);
    }
  }

  void startConsumer() {
    this.executor.submit(this::consumeQueue);
  }

  private void consumeQueue() {
    while (true) {
      take().run();
    }
  }

  Runnable take() {
    try {
      return this.queue.take();
    } catch (InterruptedException e) {
      throw new UnchekedInterruptedException(e);
    }
  }

  Thread buildThread(Runnable r) {
    final var thread = Threads.createDaemonThread(r);
    thread.setName("SingleThreadQueueProcessor");
    return thread;
  }

  @Override
  public void close() throws Exception {
    this.executor.close();
  }
}
