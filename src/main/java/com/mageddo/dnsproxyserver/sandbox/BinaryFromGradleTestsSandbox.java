package com.mageddo.dnsproxyserver.sandbox;

import com.mageddo.commons.exec.CommandLines;
import com.mageddo.commons.exec.NopResultHandler;
import com.mageddo.wait.Wait;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;

@Slf4j
public class BinaryFromGradleTestsSandbox {
  public Instance run(String[] args) {
    final var javaCommandPath = findJavaCommand();
    final var executablePath = DpsBinaryExecutableFinder.find();

    final var commandLine = new CommandLine(javaCommandPath)
      .addArgument("-jar")
      .addArgument(executablePath.toFile().toString())
      .addArguments(args);

    final var result = CommandLines.exec(commandLine, new NopResultHandler());
    final var instance = Instance.of(result);
    this.waitForStartup(instance);
    return instance;
  }

  private static String findJavaCommand() {
    return ProcessHandle.current()
      .info()
      .command()
      .orElseThrow(() -> new IllegalStateException("Couldn't find current java process command"))
      ;
  }

  private void waitForStartup(Instance instance) {
    instance.getResult().watchOutputInDaemonThread();
    new Wait()
      .ignoreException(IllegalArgumentException.class)
      .until(() -> {
        instance.sendHealthCheckStreamCommand();
        return true;
      });
  }


}
