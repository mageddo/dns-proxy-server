package com.mageddo.dnsproxyserver.config.dataprovider;

import com.fasterxml.jackson.databind.JsonNode;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJson;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJsonV1;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJsonV2;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJsonV2.Entry;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJsonV2.Env;
import com.mageddo.json.JsonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class JsonConfigs {

  public static final int VERSION_1 = 1;
  public static final int VERSION_2 = 2;
  public static List<Integer> supportedVersions = List.of(VERSION_1, VERSION_2);

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
    final var version = findVersion(tree);
    return switch (version) {
      case VERSION_2 -> objectMapper.treeToValue(tree, ConfigJsonV2.class);
      case VERSION_1 -> objectMapper.treeToValue(tree, ConfigJsonV1.class).toConfigV2();
      default -> throw new UnsupportedOperationException(String.format(
        "unsupported config file version=%d, supported=%s", version, supportedVersions
      ));
    };

  }

  @SneakyThrows
  static void createDefault(Path configPath) {
    final var config = new ConfigJsonV2();

    Files.createDirectories(configPath.getParent()); // ensure directories are created

    config
      .get_envs()
      .add(
        new Env()
          .setName(Config.Env.DEFAULT_ENV)
          .setHostnames(Collections.singletonList(Entry.sample()))
      );

    JsonUtils
      .prettyInstance()
      .writeValue(configPath.toFile(), config);
  }

  @SneakyThrows
  public static void write(Path configPath, ConfigJsonV2 config) {
    final var version = findVersion(configPath);
    final var backupPath = buildBackupPath(configPath);
    if (Objects.equals(version, VERSION_1)) {
      log.warn("status=migratingFromVersion1To2, file={}, backup={}", configPath, backupPath);
      Files.copy(configPath, backupPath);
    }
    JsonUtils
      .prettyInstance()
      .writeValue(configPath.toFile(), config)
    ;
    log.info("status=configWritten, file={}", configPath);
  }

  public static Path buildBackupPath(Path configPath) {
    return Paths.get(configPath.toAbsolutePath() + ".bkp");
  }

  @SneakyThrows
  public static Integer findVersion(Path configPath) {
    final var node = JsonUtils
      .instance()
      .readTree(configPath.toFile());
    return findVersion(node);
  }

  public static Integer findVersion(JsonNode tree) {
    return tree.at("/version").asInt(VERSION_1);
  }

  public static ConfigJsonV2 loadConfigJson() {
    final var configPath = Configs
      .getInstance()
      .getConfigFileRelativePath();
    return (ConfigJsonV2) loadConfig(configPath);
  }
}
