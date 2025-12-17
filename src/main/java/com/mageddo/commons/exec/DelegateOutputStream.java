package com.mageddo.commons.exec;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Stream;

public class DelegateOutputStream extends OutputStream {

  private final List<OutputStream> delegateOuts;

  public DelegateOutputStream(OutputStream... delegateOuts) {
    this.delegateOuts = Stream.of(delegateOuts)
        .toList();
  }

  public DelegateOutputStream(List<OutputStream> delegateOuts) {
    this.delegateOuts = delegateOuts;
  }

  @Override
  public void write(int b) throws IOException {
    for (final var delegateOut : this.delegateOuts) {
      delegateOut.write(b);
    }
  }

  @Override
  public void close() throws IOException {
    for (final var out : this.delegateOuts) {
      try {
        out.close();
      } catch (IOException e) {
      }
    }
  }
}
