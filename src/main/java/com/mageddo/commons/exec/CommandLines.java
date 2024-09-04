package com.mageddo.commons.exec;

import com.mageddo.wait.Wait;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DaemonExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.Validate;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.function.Supplier;

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

    final var stream = new PipedStream();
    final var executor = createExecutor();
    final var streamHandler = new PumpStreamHandler(stream.getOut());
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
      .stream(stream)
      .out(stream.getBout())
      .exitCode(exitCode)
      .processSupplier(executor::getProcess)
      .build();
  }

  public static Result exec(CommandLine commandLine, ExecuteResultHandler handler) {
    final var stream = new PipedStream();
    final var executor = createExecutor();
    final var streamHandler = new PumpStreamHandler(stream.getOut());
    executor.setStreamHandler(streamHandler);
    try {
      executor.setWatchdog(new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT));
      executor.execute(commandLine, handler);
    } catch (ExecuteException e) {
      handler.onProcessFailed(e);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return Result
      .builder()
      .executor(executor)
      .processSupplier(executor::getProcess)
      .out(stream.getBout())
      .stream(stream)
      .build();
  }

  public static Result exec(Request request) {
    final var stream = new PipedStream();
    final var executor = createExecutor();
    executor.setStreamHandler(request.getStreamHandler());
    Integer exitCode = null;
    try {
      executor.setWatchdog(new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT));
      if (request.getHandler() != null) {
        executor.execute(request.getCommandLine(), request.getHandler());
      } else {
        exitCode = executor.execute(request.getCommandLine());
      }
    } catch (ExecuteException e) {
      if(request.getHandler() != null){
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
      .out(stream.getBout())
      .stream(stream)
      .exitCode(exitCode)
      .build();
  }

  private static ProcessAccessibleDaemonExecutor createExecutor() {
    return new ProcessAccessibleDaemonExecutor();
  }

  @Getter
  static class ProcessAccessibleDaemonExecutor extends DaemonExecutor {

    private Process process = null;

    @Override
    protected Process launch(CommandLine command, Map<String, String> env, File dir) throws IOException {
      return this.process = super.launch(command, env, dir);
    }
  }


  @Value
  @Builder
  public static class Request {
    CommandLine commandLine;
    ExecuteResultHandler handler;
    PumpStreamHandler streamHandler;
  }

  @Getter
  @Builder
  @ToString(of = {"exitCode"})
  public static class Result {

    @NonNull
    private Executor executor;

    @NonNull
    private PipedStream stream;

    @NonNull
    private ByteArrayOutputStream out;

    @NonNull
    private Supplier<Process> processSupplier;

    private Integer exitCode;

    public void watchOutputInDaemonThread() {
      final var task = (Runnable) () -> {
        final var bf = new BufferedReader(new InputStreamReader(this.stream.getPipedInputStream()));
        while (true) {
          try {
            final var line = bf.readLine();
            if (line == null) {
              log.debug("status=outputEnded");
              break;
            }
            log.debug(">>> {}", line);
          } catch (IOException e) {

          }
        }
      };
      Thread
        .ofVirtual()
        .start(task);
    }

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

    @SneakyThrows
    public Process getProcess() {
      return this.processSupplier.get();
    }

    public Long getProcessId() {
      final var process = this.getProcess();
      if (process == null) {
        return null;
      }
      return process.pid();
    }

    public void waitProcessToFinish() {
      new Wait<>()
        .infinityTimeout()
        .ignoreException(IllegalArgumentException.class)
        .until(() -> {
          Validate.isTrue(this.isProcessFinished(), "Process not finished yet");
          return true;
        });
    }

    private boolean isProcessFinished() {
      return getProcess() != null && !getProcess().isAlive();
    }

    public Integer getProcessExitCodeWhenAvailable() {
      try {
        return getProcess().exitValue();
      } catch (IllegalThreadStateException e) {
        return null;
      }
    }
  }
}
