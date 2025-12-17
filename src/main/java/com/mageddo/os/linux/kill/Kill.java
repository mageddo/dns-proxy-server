package com.mageddo.os.linux.kill;

import com.mageddo.commons.exec.CommandLines;

import org.apache.commons.exec.CommandLine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Kill {
  public static void sendSignal(int signal, long pid) {
    final var result = CommandLines.exec(
        new CommandLine("kill")
            .addArgument("-" + signal)
            .addArgument(pid + "")
    );
    result.checkExecution();
    log.debug("status=signalSentWithSuccess, signal={}, pid={}", signal, pid);
  }
}
