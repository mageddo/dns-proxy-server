package com.mageddo.dnsproxyserver.config;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class SolverLocal {
  private String activeEnv;
  private List<Config.Env> envs;
}
