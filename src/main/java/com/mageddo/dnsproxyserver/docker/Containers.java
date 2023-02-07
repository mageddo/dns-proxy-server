package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.model.Container;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Containers {

  public static String toNames(List<Container> containers) {
    return containers
      .stream()
      .map(Containers::firstNameOrId)
      .collect(Collectors.joining(", "));
  }

  public static String firstNameOrId(Container c) {
    return Stream.of(c.getNames()).findFirst().orElse(c.getId());
  }

}
