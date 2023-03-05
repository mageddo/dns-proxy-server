package com.mageddo.windows;

public class Main {
  public static void main(String[] args) {
    Networks.findNetworksNames().forEach(it -> System.out.println(it));
  }
}
