package com.mageddo.commons.exec;

import com.mageddo.io.LogPrinter;
import com.mageddo.wait.Wait;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DaemonExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.Validate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    final var bout = new ByteArrayOutputStream();
    final var stream = new PipedStream(bout);
    final var executor = createExecutor();
    executor.setStreamHandler(new PumpStreamHandler(stream));
    int exitCode;
    try {
      executor.setWatchdog(new ExecuteWatchdog(timeout));
      exitCode = executor.execute(commandLine);
      registerProcessWatch(executor);
    } catch (ExecuteException e) {
      exitCode = e.getExitValue();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return Result
      .builder()
      .executor(executor)
      .out(bout)
      .exitCode(exitCode)
      .processSupplier(executor::getProcess)
      .build();
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
      executor.setWatchdog(new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT));
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

  @Getter
  static class ProcessAccessibleDaemonExecutor extends DaemonExecutor {

    private Process process = null;

    @Override
    protected Process launch(CommandLine command, Map<String, String> env, File dir) throws IOException {
      return this.process = super.launch(command, env, dir);
    }
  }

  @Value
  @Builder(toBuilder = true, builderClassName = "RequestBuilder", buildMethodName = "build0")
  public static class Request {

    private final CommandLine commandLine;
    private final ExecuteResultHandler handler;
    private Map<String, String> env;

    @NonFinal
    private boolean watchingOutput;

    @Builder.Default
    private final Streams streams = Streams.builder()
      .outAndErr(new ByteArrayOutputStream())
      .build();

    public ExecuteStreamHandler getStreamHandler() {
      return this.streams.toStreamHandler();
    }

    private Request printOutToLogsInBackground() {
      if (this.watchingOutput) {
        throw new IllegalStateException("Already watching output");
      }
      this.watchingOutput = true;
      LogPrinter.printInBackground(this.streams.outAndErr.getPipedIn());
      return this;
    }

    public OutputStream getBestOut() {
      return this.streams.getBestOriginalOutput();
    }

    public static class RequestBuilder {

      private boolean printLogsInBackground = false;

      public Request build() {
        final var request = this.build0();
        if (this.printLogsInBackground) {
          request.printOutToLogsInBackground();
        }
        return request;
      }

      public RequestBuilder printLogsInBackground() {
        this.printLogsInBackground = true;
        return this;
      }
    }


    @Value
    @Builder(toBuilder = true, builderClassName = "StreamsBuilder")
    public static class Streams {

      private final PipedStream outAndErr;
      private final OutputStream out;
      private final OutputStream err;
      private final InputStream input;

      public PipedStream getBestOut() {
        if (this.outAndErr != null) {
          return this.outAndErr;
        }
        throw new UnsupportedOperationException();
      }

      public OutputStream getBestOriginalOutput() {
        return this.getBestOut();
      }

      public static class StreamsBuilder {
        public StreamsBuilder outAndErr(OutputStream outAndErr) {
          this.outAndErr = new PipedStream(outAndErr);
          return this;
        }
      }

      public ExecuteStreamHandler toStreamHandler() {
        if (this.outAndErr != null) {
          return new PumpStreamHandler(this.outAndErr);
        }
        return new PumpStreamHandler(this.out, this.err, this.input);
      }
    }
  }

  @Getter
  @Builder
  @ToString(of = {"exitCode"})
  public static class Result {

    @NonNull
    private Request request;

    @NonNull
    private Executor executor;

    @NonNull
    private OutputStream out;

    @NonNull
    private Supplier<Process> processSupplier;

    private Integer exitCode;

    public Result printOutToLogsInBackground() {
      this.request.printOutToLogsInBackground();
      return this;
    }

    public String getOutAsString() {
      Validate.isTrue(this.out instanceof ByteArrayOutputStream, "Only ByteArrayOutputStream is supported");
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
