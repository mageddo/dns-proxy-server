package com.mageddo.dnsproxyserver.sandbox;

import com.mageddo.commons.exec.CommandLines;
import com.mageddo.commons.exec.NopResultHandler;
import com.mageddo.commons.exec.Request;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigEnv;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Map;

@Slf4j
public class BinaryFromGradleTestsSandbox {
  public Instance run(Path configFile) {
    final var commandLine = DpsBinaryExecutableFinder.buildCommandLine();
    final var request = Request.builder()
      .commandLine(commandLine)
      .handler(new NopResultHandler())
      .env(Map.of(ConfigEnv.MG_CONFIG_FILE_PATH, configFile.toString()))
      .build();

    final var result = CommandLines.exec(request)
      .printOutToLogsInBackground();

    return Instance.of(result);
  }

}
