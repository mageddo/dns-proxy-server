package com.mageddo.commons.exec;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UncheckedIOException;

import lombok.Getter;

public class PipedStream extends OutputStream {

  @Getter
  private final PipedInputStream pipedIn;

  private final DelegateOutputStream delegateOut;
  private final OutputStream originalOut;

  public PipedStream(final OutputStream out) {
    try {
      this.pipedIn = new PipedInputStream();
      this.originalOut = out;
      final var pout = new PipedOutputStream(this.pipedIn);
      this.delegateOut = new DelegateOutputStream(out, pout);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void write(int b) throws IOException {
    this.delegateOut.write(b);
  }

  public void close() throws IOException {
    this.delegateOut.close();
  }

  OutputStream getOriginalOut() {
    return originalOut;
  }
}
