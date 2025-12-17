package com.mageddo.commons.exec;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.function.Supplier;

import com.mageddo.wait.Wait;

import org.apache.commons.exec.Executor;
import org.apache.commons.lang3.Validate;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;

@Getter
@Builder
@ToString(of = {"exitCode"})
public class Result {

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
    Validate.isTrue(this.out instanceof ByteArrayOutputStream,
        "Only ByteArrayOutputStream is supported"
    );
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
