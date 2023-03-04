package com.mageddo.os.osx;

import com.mageddo.commons.exec.CommandLines;

import java.util.List;
import java.util.stream.Stream;

public class Networks {
  public static List<String> findNetworksNames(){
    final var lines = CommandLines
      .exec("networksetup -listallnetworkservices")
      .checkExecution()
      .getOutAsString()
      .split("\\r?\\n");

    return Stream.of(lines)
      .skip(1)
      .filter(it -> !it.contains("*"))
      .toList()
      ;
  }

  public static void main(String[] args) {
    System.out.println(findNetworksNames());
  }
}
