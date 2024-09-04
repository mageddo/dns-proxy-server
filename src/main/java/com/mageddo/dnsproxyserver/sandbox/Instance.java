package com.mageddo.dnsproxyserver.sandbox;

import com.mageddo.commons.exec.CommandLines;
import com.mageddo.os.linux.kill.Kill;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

@Slf4j
@Value
@Builder
public class Instance {

  @NonNull
  CommandLines.Result result;

  public static Instance of(CommandLines.Result result) {
    return Instance.builder()
      .result(result)
      .build()
      ;
  }

  public void sendHealthCheckStreamCommand() {
    this.sendHealthCheckStreamCommandValidatingResult();
    this.validateIsHealth();
  }

  public void sendHealthCheckSignal() {
    this.sendHealthCheckSignalValidatingResult();
    this.validateIsHealth();
  }

  private void validateIsHealth() {
    final var out = this.getResult().getOutAsString();
    final var isHealth = out.contains("dps.healthCheck.health=true");
    Validate.isTrue(isHealth, "App not health yet, content=%s", out);
  }

  private void sendHealthCheckSignalValidatingResult() {
    final var processId = this.getProcessId();
    Validate.isTrue(processId != null, "Process not started yet");
    log.trace("is alive: {}, exitcode={}", this.result.getProcess().isAlive(), this.result.getProcessExitCodeWhenAvailable());
    Kill.sendSignal(10, processId);
  }

  private void sendHealthCheckStreamCommandValidatingResult() {
    this.result.getExecutor().getStreamHandler().setProcessErrorStream();
    final var processId = this.getProcessId();
    Validate.isTrue(processId != null, "Process not started yet");
    log.trace("is alive: {}, exitcode={}", this.result.getProcess().isAlive(), this.result.getProcessExitCodeWhenAvailable());
    Kill.sendSignal(10, processId);
  }

  private Long getProcessId() {
    return this.result.getProcessId();
  }
}
