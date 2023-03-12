package com.mageddo.http;

import com.mageddo.commons.regex.Regexes;

import java.util.Collection;
import java.util.regex.Pattern;

class Wildcards {

  public static final String ALL_SUB_PATHS_WILDCARD = ".*";

  public static String findMatchingMap(Collection<String> map, String path) {
    return map
      .stream()
      .map(Path::of)
      .sorted((o1, o2) -> {
        final var o1Index = indexOfWildcard(o1);
        final var o2Index = indexOfWildcard(o2);
        return Integer.compare(o1Index, o2Index);
      })
      .map(Path::getRaw)
      .filter(mapPath -> {
        final var matcher = Regexes.matcher(path, Pattern.compile(mapPath));
        return matcher != null && matcher.matches();
      })
      .findFirst()
      .orElse(null);
  }

  static int indexOfWildcard(Path p) {
    final var i = p.indexOf(ALL_SUB_PATHS_WILDCARD);
    return i == -1 ? Integer.MAX_VALUE : i;
  }
}
