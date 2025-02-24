package com.mageddo.dnsproxyserver.config;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class SolverLocal {
  private String activeEnv;
  private List<Config.Env> envs;

  public Config.Env getFirst() {
    if (this.envs == null || this.envs.isEmpty()) {
      return null;
    }
    return this.envs.get(0);
  }
}
