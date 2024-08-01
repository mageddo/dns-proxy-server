package com.mageddo.concurrent;

import com.mageddo.commons.concurrent.Threads;
import com.mageddo.commons.lang.exception.UnchekedInterruptedException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SingleThreadQueueProcessor implements AutoCloseable {

  private final BlockingQueue<Runnable> queue;
  private final ExecutorService executor;
  private final AtomicInteger processedCounter = new AtomicInteger(0);

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
      final var r = take();
      r.run();
      this.processedCounter.getAndIncrement();
      log.trace("status=processed, count={}, task={}", this.getProcessedCount(), r);
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
    thread.setName("queueProcessor");
    return thread;
  }

  @Override
  public void close() throws Exception {
    this.executor.close();
  }

  public int getProcessedCount() {
    return this.processedCounter.get();
  }
}
