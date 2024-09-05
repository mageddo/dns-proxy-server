package com.mageddo.dnsproxyserver.sandbox;

import com.mageddo.commons.exec.Result;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
@Builder
public class Instance {

  @NonNull
  Result result;

  public static Instance of(Result result) {
    return Instance.builder()
      .result(result)
      .build()
      ;
  }
}
