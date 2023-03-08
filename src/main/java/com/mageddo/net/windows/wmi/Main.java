package com.mageddo.net.windows.wmi;

public class Main {

  public static void main(String[] args) {


    final var wmi = new NetworkWmi();
    final var interfaces = wmi.findInterfaces();
    interfaces.forEach(System.out::println);

  }

}
