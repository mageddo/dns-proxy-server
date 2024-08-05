package com.mageddo.dnsproxyserver;

import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.solver.SimpleResolver;
import com.mageddo.dnsproxyserver.utils.Ips;
import com.mageddo.net.SocketUtils;
import com.mageddo.utils.Executors;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppIntTest {
  @Test
  void appMustStart() throws IOException {

    final var webServerPort = SocketUtils.findRandomFreePort();
    final var dnsServerPort = SocketUtils.findRandomFreePort();
    final var hostToQuery = "dps-sample.dev";

    final var config = new String[]{
      "--default-dns=false",
      "--web-server-port=" + webServerPort,
      "--server-port=" + dnsServerPort,
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
