package com.mageddo.utils;

import java.nio.ByteBuffer;

public class Shorts {
  public static byte[] toBytes(short s){
    return ByteBuffer
      .allocate(2)
      .putShort(s)
      .array()
      ;
  }
}
