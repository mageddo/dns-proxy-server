package com.mageddo.utils;

public class Bytes {
  public static byte[] toNative(Byte[] arr) {
    final var newarr = new byte[arr.length];
    for (int i = 0; i < newarr.length; i++) {
      newarr[i] = arr[i];
    }
    return newarr;
  }

  public static byte[] toNative(Integer[] arr) {
    final var newarr = new byte[arr.length];
    for (int i = 0; i < newarr.length; i++) {
      newarr[i] = arr[i].byteValue();
    }
    return newarr;
  }
}
