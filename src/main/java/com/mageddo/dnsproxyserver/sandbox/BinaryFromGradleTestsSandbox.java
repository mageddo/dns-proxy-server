package com.mageddo.dnsproxyserver.sandbox;

import com.mageddo.commons.exec.CommandLines;
import org.apache.commons.exec.CommandLine;

public class BinaryFromGradleTestsSandbox {
  public Instance run(String[] args) {
    final var executablePath = DpsBinaryExecutableFinder.find();
    final var commandLine = new CommandLine(executablePath.toFile())
      .addArguments(args);
    return Instance.of(CommandLines.exec(commandLine));
  }


}
