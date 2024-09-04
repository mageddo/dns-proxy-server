package com.mageddo.dnsproxyserver.sandbox;

import com.mageddo.commons.concurrent.Threads;
import com.mageddo.commons.exec.CommandLines;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;

import java.time.Duration;

public class BinaryFromGradleTestsSandbox {
  public Instance run(String[] args) {
    final var javaCommandPath = findJavaCommand();
    final var executablePath = DpsBinaryExecutableFinder.find();

    final var commandLine = new CommandLine(javaCommandPath)
      .addArgument("-jar")
      .addArgument(executablePath.toFile().toString())
      .addArguments(args);

    final var executor = CommandLines.exec(commandLine, new ExecuteResultHandler() {
      public void onProcessComplete(int exitValue) {

      }

      public void onProcessFailed(ExecuteException e) {

      }
    });
    final var instance = Instance.of(executor);
    executor.getWatchdog().;

    waitForStartup(instance);
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
    Threads.sleep(Duration.ofSeconds(2));
    System.out.println(instance.getExecutor().getOutAsString());
  }


}
