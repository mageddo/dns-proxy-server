package testing.templates.config;

import java.nio.file.Paths;

import com.mageddo.dnsproxyserver.config.dataformat.v2.legacyenv.ConfigEnv;

public class ConfigEnvTemplates {
  public static ConfigEnv withConfigFilePath() {
    return ConfigEnv
        .builder()
        .configFilePath(Paths.get("some-place/config.json"))
        .build();
  }

  public static ConfigEnv empty() {
    return ConfigEnv
        .builder()
        .build();
  }

  public static ConfigEnv withStubSolverDomainName() {
    return ConfigEnv
        .builder()
        .solverStubDomainName("acme")
        .build();
  }
}
