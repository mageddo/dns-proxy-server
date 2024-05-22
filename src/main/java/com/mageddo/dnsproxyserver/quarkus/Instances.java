package com.mageddo.dnsproxyserver.quarkus;

import com.mageddo.di.InstanceImpl;

import javax.enterprise.inject.Instance;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Instances {
  public static <T> List<T> toList(Instance<T> instance) {
    return instance
      .stream()
      .toList()
      ;
  }

  public static <T> Set<T> toSet(Instance<T> instance) {
    return instance
      .stream()
      .collect(Collectors.toSet())
      ;
  }

  public static <T> Instance<T> of(T... instances) {
    return new InstanceImpl<>(Stream.of(instances).toList());
  }
}
