package com.mageddo.commons.exec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UncheckedIOException;

public class PipedStream {

  private final PipedInputStream pipedInputStream;
  private final ByteArrayOutputStream bout;
  private final DelegateOutputStream out;

  public PipedStream() {
    try {
      this.pipedInputStream = new PipedInputStream();
      final var pout = new PipedOutputStream(this.pipedInputStream);
      this.bout = new ByteArrayOutputStream();
      this.out = new DelegateOutputStream(this.bout, pout);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public OutputStream getOut() {
    return this.out;
  }

  public ByteArrayOutputStream getBout() {
    return bout;
  }

  public PipedInputStream getPipedInputStream() {
    return this.pipedInputStream;
  }

  public void close() throws IOException {
    this.out.close();
  }
}
