package com.mageddo.http;

import com.mageddo.commons.regex.Regexes;

import java.util.Collection;
import java.util.regex.Pattern;

class Wildcards {

  public static final String ALL_SUB_PATHS_WILDCARD = ".*";

  public static String findMatchingMap(Collection<String> map, String rawPath) {
    return map
      .stream()
      .map(Path::of)
      .sorted((o1, o2) -> {
        final var o1Index = indexOfWildcard(o1);
        final var o2Index = indexOfWildcard(o2);
        return Integer.compare(o1Index, o2Index);
      })
      .filter(key -> {
//        /hello-world/.*

//        /hello-world/batata
        return Regexes
          .matcher(rawPath, Pattern.compile(key.getRaw()))
          .matches();
      })
      .map(Path::getRaw)
      .findFirst()
      .orElse(null);
  }

  static int indexOfWildcard(Path p) {
    final var i = p.indexOf(ALL_SUB_PATHS_WILDCARD);
    return i == -1 ? Integer.MAX_VALUE : i;
  }
}
