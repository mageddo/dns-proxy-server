package com.mageddo.windows.jna;

import com.mageddo.windows.Networks;

import java.util.Arrays;

public class Main {

  public static void main(String[] args) {
    final var r = Networks.findNetworkInterfaces();
    System.out.println(r.dwNumEntries);
    System.out.println(Arrays.toString(r.table));
  }
}
