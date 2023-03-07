package com.mageddo.commons.lang;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class CloseQueue implements AutoCloseable {

  private final List<Closeable> closeables = new ArrayList<>();

  public CloseQueue add(Closeable c) {
    this.closeables.add(c);
    return this;
  }

  public List<Closeable> getCloseables() {
    return Collections.unmodifiableList(this.closeables);
  }

  @Override
  public void close() {
    this.closeables.forEach(CloseQueue::silentClose);
  }

  public static CloseQueue build() {
    return new CloseQueue();
  }

  static void silentClose(Closeable c) {
    try {
      c.close();
    } catch (IOException e) {
      log.info("status=couldn't close, msg={}", e.getMessage());
    }
  }
}
