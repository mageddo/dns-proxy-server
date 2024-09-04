package com.mageddo.dnsproxyserver.sandbox;

import com.mageddo.commons.exec.CommandLines;
import com.mageddo.commons.exec.NopResultHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
public class BinaryFromGradleTestsSandbox {
  public Instance run(Path configFile) {
    final var commandLine = DpsBinaryExecutableFinder.buildCommandLine(configFile);
    final var result = CommandLines.exec(commandLine, new NopResultHandler());
    return Instance.of(result);
  }

}
