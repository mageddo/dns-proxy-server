package com.mageddo.dnsproxyserver.sandbox;

import com.mageddo.commons.concurrent.Threads;
import com.mageddo.commons.exec.CommandLines;
import org.apache.commons.exec.CommandLine;

import java.time.Duration;

public class BinaryFromGradleTestsSandbox {
  public Instance run(String[] args) {
    final var executablePath = DpsBinaryExecutableFinder.find();
    final var commandLine = new CommandLine(executablePath.toFile())
      .addArguments(args);
    final var instance = Instance.of(CommandLines.exec(commandLine));
    waitForStartup(instance);
    return instance;
  }

  private void waitForStartup(Instance instance) {
    Threads.sleep(Duration.ofSeconds(2));
  }


}
