package com.mageddo.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Fields {
  public static List<String> findNonStaticFieldNames(Class<?> clazz) {
    final List<String> fieds = new ArrayList<>();
    for (final Field f : clazz.getDeclaredFields()) {
      if (!f.isSynthetic() && !Modifier.isStatic(f.getModifiers())) {
        fieds.add(f.getName());
      }
    }
    return fieds;
  }
}
