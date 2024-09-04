package com.mageddo.commons.exec;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class DelegateOutputStreamTest {
  @Test
  void mustWriteToTheTwoOuts() throws IOException {

    final var out1 = new ByteArrayOutputStream();
    final var out2 = new ByteArrayOutputStream();
    final var arr = new byte[]{1, 2, 3};

    // act
    final var delegateOut = new DelegateOutputStream(out1, out2);
    delegateOut.write(arr);

    // assert
    assertArrayEquals(arr, out1.toByteArray());
    assertArrayEquals(arr, out2.toByteArray());
  }
}
