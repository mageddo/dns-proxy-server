package com.mageddo.os.linux.kill;

import com.mageddo.commons.exec.CommandLines;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;

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
