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

  public static Short[] toUnsignedShortArray(byte[] source) {

    if (source == null) {
      return null;
    }

    final var target = new Short[source.length];
    for (int i = 0; i < target.length; i++) {
      target[i] = (short) Byte.toUnsignedInt(source[i]);
    }
    return target;
  }

  public static byte[] toNative(Short[] source) {
    if (source == null) {
      return null;
    }
    final var newarr = new byte[source.length];
    for (int i = 0; i < newarr.length; i++) {
      newarr[i] = source[i].byteValue();
    }
    return newarr;
  }
}
