package com.mageddo.dnsproxyserver.sandbox;

import com.mageddo.commons.exec.CommandLines;
import com.mageddo.commons.exec.NopResultHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BinaryFromGradleTestsSandbox {
  public Instance run(String[] args) {
    final var commandLine = DpsBinaryExecutableFinder.buildCommandLine()
        .addArguments(args);
    final var result = CommandLines.exec(commandLine, new NopResultHandler());
    return Instance.of(result);
  }

  private static String findJavaCommand() {
    return ProcessHandle.current()
        .info()
        .command()
        .orElseThrow(() -> new IllegalStateException("Couldn't find current java process command"))
        ;
  }


}
