package com.mageddo.commons.exec;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class PipedStreamTest {

  @Test
  void mustWriteToOutAndBeAbleToReadWhatIsBeingWritten() throws IOException {
    // arrange
    final var bytes = new byte[]{1, 2, 3};

    // act
    final var stream = new PipedStream();
    stream.getOut().write(bytes);
    stream.close();

    // assert
    assertArrayEquals(bytes, stream.getBout().toByteArray());
    assertArrayEquals(bytes, stream.getPipedInputStream().readAllBytes());
  }
}
