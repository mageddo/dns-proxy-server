package com.mageddo.dnsproxyserver.systemd;

import com.mageddo.commons.exec.CommandLines;
import org.apache.commons.lang3.Validate;

public class ResolvedService {
  public static void restart() {
    final var result = CommandLines.exec("service systemd-resolved restart");
    Validate.isTrue(
      result.getExitCode() == 0,
      "Not possible to restart resolved service: %d : %s",
      result.getExitCode(), result.getOutAsString()
    );
  }
}
