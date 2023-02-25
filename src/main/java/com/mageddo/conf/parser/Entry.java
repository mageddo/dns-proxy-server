package com.mageddo.conf.parser;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Entry {

  @NonNull
  EntryType type;

  @NonNull
  String line;

}
