package com.mageddo.dnsproxyserver.in.entrypoint;

import com.mageddo.commons.concurrent.Threads;
import com.mageddo.di.Eager;
import com.mageddo.dnsproxyserver.healthcheck.entrypoint.HealthCheckStreamEntrypoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.NoSuchElementException;
import java.util.Scanner;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class InputStreamEntrypoint implements Eager {

  private final HealthCheckStreamEntrypoint healthCheckStreamEntrypoint;
  private boolean inShell;

  @Override
  public void run() {
    Thread.ofVirtual()
      .start(this::readInputStream)
    ;
  }

  private void readInputStream() {
    log.debug("status=scanningInputStream");
    final var scanner = new Scanner(System.in);
    while (true) {
      final var line = safeReadLine(scanner);
      if (line == null) {
        break;
      }
      this.checkHealthCheck(line);
      Threads.sleep(1000 / 15);
    }
  }

  private static String safeReadLine(Scanner scanner) {
    try {
      return scanner.nextLine();
    } catch (NoSuchElementException e) {
      return null;
    }
  }

  private void checkHealthCheck(String line) {
    if (line.equals("shell")) {
      this.inShell = true;
    }
    if (this.inShell && line.equals("dps.healthCheck.health")) {
      this.healthCheckStreamEntrypoint.printHealthCheck();
    }
  }
}
