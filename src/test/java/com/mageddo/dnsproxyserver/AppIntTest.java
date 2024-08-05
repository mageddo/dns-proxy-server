package com.mageddo.dnsproxyserver;

import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.solver.SimpleResolver;
import com.mageddo.dnsproxyserver.utils.Ips;
import com.mageddo.net.SocketUtils;
import com.mageddo.utils.Executors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppIntTest {
  @Test
  void appMustStart(@TempDir Path tmpPath) throws IOException {

    final var webServerPort = SocketUtils.findRandomFreePort();
    final var dnsServerPort = SocketUtils.findRandomFreePort();
    final var configPath = tmpPath.resolve("config.json");
//    final var logPath = tmpPath.resolve("logs.txt");
    final var logPath = Paths.get("/tmp/logs.txt");
    final var hostToQuery = "dps-sample.dev";

    final var config = new String[]{
      "--default-dns=false",
      "--web-server-port=" + webServerPort,
      "--server-port=" + dnsServerPort,
      "--conf-path=" + configPath,
      "--log-file=" + logPath,
    };
    final var app = new App(config);

    try (final var executor = Executors.newThreadExecutor()) {
      executor.submit(app::start);

      Threads.sleep(Duration.ofSeconds(2));

      final var dnsServerAddress = Ips.getAnyLocalAddress(dnsServerPort);

      final var dnsClient = new SimpleResolver(dnsServerAddress);
      final var res = dnsClient.send(Messages.aQuestion(hostToQuery));

      assertTrue(Messages.isSuccess(res));

    }


  }
}
