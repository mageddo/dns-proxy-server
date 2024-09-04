package com.mageddo.commons.exec;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;

@Slf4j
public class CommandLines {

  public static Result exec(String commandLine, Object... args) {
    return exec(CommandLine.parse(String.format(commandLine, args)),
      ExecuteWatchdog.INFINITE_TIMEOUT
    );
  }

  public static Result exec(long timeout, String commandLine, Object... args) {
    return exec(CommandLine.parse(String.format(commandLine, args)), timeout);
  }

  public static Result exec(CommandLine commandLine) {
    return exec(commandLine, ExecuteWatchdog.INFINITE_TIMEOUT);
  }

  public static Result exec(CommandLine commandLine, long timeout) {
    return exec(
      Request.builder()
        .commandLine(commandLine)
        .timeout(Duration.ofMillis(timeout))
        .build()
    );
  }

  private static void registerProcessWatch(ProcessAccessibleDaemonExecutor executor) {
    ProcessesWatchDog.instance()
      .watch(executor::getProcess)
    ;
  }

  public static Result exec(CommandLine commandLine, ExecuteResultHandler handler) {
    return exec(Request
      .builder()
      .commandLine(commandLine)
      .handler(handler)
      .build()
    );
  }

  public static Result exec(Request request) {
    final var executor = createExecutor();
    executor.setStreamHandler(request.getStreamHandler());
    Integer exitCode = null;
    try {
      executor.setWatchdog(new ExecuteWatchdog(request.getTimeoutInMillis()));
      if (request.getHandler() != null) {
        executor.execute(request.getCommandLine(), request.getEnv(), request.getHandler());
        registerProcessWatch(executor);
      } else {
        exitCode = executor.execute(request.getCommandLine(), request.getEnv());
      }
    } catch (ExecuteException e) {
      if (request.getHandler() != null) {
        request.getHandler().onProcessFailed(e);
      } else {
        exitCode = e.getExitValue();
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return Result
      .builder()
      .executor(executor)
      .processSupplier(executor::getProcess)
      .out(request.getBestOut())
      .exitCode(exitCode)
      .request(request)
      .build();
  }

  private static ProcessAccessibleDaemonExecutor createExecutor() {
    return new ProcessAccessibleDaemonExecutor();
  }

}
