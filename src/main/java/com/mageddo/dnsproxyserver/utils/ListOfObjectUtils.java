package com.mageddo.dnsproxyserver.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ListOfObjectUtils {

  public static <R, T> List<R> mapField(Function<T, R> fn, List<T> listOfObjects) {
    return listOfObjects
      .stream()
      .map(fn)
      .toList()
      ;
  }

  public static <T, R> List<R> mapField(Function<T, R> fn, List<T> listOfObjects, R def) {
    final var nList = new ArrayList<>(mapField(fn, listOfObjects));
    nList.add(def);
    return nList;
  }
}
