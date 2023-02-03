package com.mageddo.dnsproxyserver.config.entrypoint;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.json.JsonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class JsonConfigs {

  /**
   * Parser v1 or v2 config json then return the interface.
   *
   * @param configPath
   */
  @SneakyThrows
  public static ConfigJson loadConfig(Path configPath) {

    if (!Files.exists(configPath)) {
      createDefault(configPath);
    }

    final var objectMapper = JsonUtils.instance();
    final var tree = objectMapper.readTree(configPath.toFile());
    if (tree.isEmpty()) {
      log.info("status=emptyConfigFile, action=usingDefault, file={}", configPath);
      return new ConfigJsonV2();
    }
    final var version = tree.at("/version").asInt(1);

    return switch (version) {
      case 2 -> objectMapper.treeToValue(tree, ConfigJsonV2.class);
      default -> objectMapper.treeToValue(tree, ConfigJsonV1.class);
    };

  }

  @SneakyThrows
  static void createDefault(Path configPath) {
    final var config = new ConfigJsonV2();

    config
      .getEnvs()
      .add(new ConfigJsonV2.Env().setName(Config.Env.DEFAULT_ENV));

    JsonUtils
      .prettyInstance()
      .writeValue(configPath.toFile(), config);
  }
}
