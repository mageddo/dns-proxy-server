package com.mageddo.dnsproxyserver.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class SolverLocal {

  private String activeEnv;
  private List<Config.Env> envs;

  @JsonIgnore
  public Config.Env getFirst() {
    if (this.envs == null || this.envs.isEmpty()) {
      return null;
    }
    return this.envs.getFirst();
  }
}
