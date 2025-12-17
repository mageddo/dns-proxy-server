package com.mageddo.commons.exec;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DaemonExecutor;

import lombok.Getter;

@Getter
class ProcessAccessibleDaemonExecutor extends DaemonExecutor {

  private Process process = null;

  @Override
  protected Process launch(CommandLine command, Map<String, String> env, File dir)
      throws IOException {
    return this.process = super.launch(command, env, dir);
  }
}
