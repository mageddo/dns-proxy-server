package com.mageddo.commons.exec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class PipedStreamTest {

  @Test
  void mustWriteToOutAndBeAbleToReadWhatIsBeingWritten() throws IOException {
    // arrange
    final var bytes = new byte[]{1, 2, 3};

    // act
    final var stream = new PipedStream(new ByteArrayOutputStream());
    stream.write(bytes);
    stream.close();

    // assert
    final var bout = (ByteArrayOutputStream) stream.getOriginalOut();
    assertArrayEquals(bytes, bout.toByteArray());
    assertArrayEquals(bytes, stream.getPipedIn()
        .readAllBytes()
    );
  }
}
