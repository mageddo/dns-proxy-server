package com.mageddo.conf.parser;

import java.util.Set;

public interface Transformer {

  String handle(Entry entry);

  default String after(boolean fileHasContent, Set<String> foundEntryTypes) {
    return null;
  }
}
