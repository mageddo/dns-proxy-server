package com.mageddo.commons.exec;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DaemonExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

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
    final var out = new ByteArrayOutputStream();
    final var executor = new DaemonExecutor();
    final var streamHandler = new PumpStreamHandler(out);
    executor.setStreamHandler(streamHandler);
    int exitCode;
    try {
      executor.setWatchdog(new ExecuteWatchdog(timeout));
      exitCode = executor.execute(commandLine);
    } catch (ExecuteException e) {
      exitCode = e.getExitValue();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return Result
        .builder()
        .executor(executor)
        .out(out)
        .exitCode(exitCode)
        .build();
  }

  @Getter
  @Builder
  @ToString(of = {"exitCode"})
  public static class Result {

    @NonNull
    private Executor executor;

    @NonNull
    private ByteArrayOutputStream out;

    private int exitCode;

    public String getOutAsString() {
      return this.out.toString();
    }

    public Result checkExecution() {
      if (this.executor.isFailure(this.getExitCode())) {
        throw new ExecutionValidationFailedException(this);
      }
      return this;
    }

    public String toString(boolean printOut) {
      return String.format(
          "code=%d, out=%s",
          this.exitCode, printOut ? this.getOutAsString() : null
      );
    }
  }
}
