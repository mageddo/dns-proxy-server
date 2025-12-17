package com.mageddo.dnsproxyserver.config.dataformat.v3.file;

import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.application.ConfigPathMapper;
import com.mageddo.dnsproxyserver.config.dataformat.v2.cmdargs.ConfigDAOCmdArgs;
import com.mageddo.dnsproxyserver.config.dataformat.v2.legacyenv.ConfigEnv;
import com.mageddo.dnsproxyserver.utils.Envs;
import com.mageddo.dnsproxyserver.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigFilePathDAOImpl implements ConfigFilePathDAO {

  private final ConfigDAOCmdArgs configDAOCmdArgs;

  @Override
  public Path find() {
    final var workDir = this.findWorkDir();
    final var configRelativePath = this.findConfigRelativePath();
    return ConfigPathMapper.build(workDir, configRelativePath);
  }

  private Path findConfigRelativePath() {
    final var pathFromArgs = this.configDAOCmdArgs.findRaw()
        .getConfigFileAsPath();
    return ObjectUtils.firstNonNullRequiring(
        Envs.getPathOrNull("DPS_CONFIG_FILE_PATH"),
        Envs.getPathOrNull(ConfigEnv.MG_CONFIG_FILE_PATH),
        pathFromArgs
    );
  }

  private Path findWorkDir() {
    return ObjectUtils.firstNonNull(
        Envs.getPathOrNull("DPS_WORK_DIR"),
        Envs.getPathOrNull(ConfigEnv.MG_WORK_DIR)
    );
  }

}
