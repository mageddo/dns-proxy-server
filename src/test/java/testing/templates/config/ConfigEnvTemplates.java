package testing.templates.config;

import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigEnv;

import java.nio.file.Paths;

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
}
