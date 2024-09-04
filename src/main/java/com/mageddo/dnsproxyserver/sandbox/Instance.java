package com.mageddo.dnsproxyserver.sandbox;

import com.mageddo.commons.exec.CommandLines;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Instance {

  @NonNull
  CommandLines.Result result;

  public static Instance of(CommandLines.Result result) {
    return Instance.builder()
      .result(result)
      .build()
      ;
  }
}
