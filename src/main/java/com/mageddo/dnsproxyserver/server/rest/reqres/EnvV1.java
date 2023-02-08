package com.mageddo.dnsproxyserver.server.rest.reqres;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EnvV1 {

  private String name;

  public static EnvV1 of(String name) {
    return new EnvV1().setName(name);
  }
}
