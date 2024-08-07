package com.mageddo.dnsproxyserver;

import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.server.Starter;
import com.mageddo.dnsproxyserver.solver.SimpleResolver;
import com.mageddo.dnsproxyserver.utils.Ips;
import com.mageddo.utils.Executors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xbill.DNS.Message;
import testing.templates.ConfigFlagArgsTemplates;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class AppIntTest {

  @BeforeEach
  void beforeEach() {
    Starter.setMustStartFlagActive(true);
    Configs.clear();
  }

  @AfterAll
  static void afterAll(){
    Starter.setMustStartFlagActive(false);
  }

  @Test
  void appMustStartAndQuerySampleWithSuccess() {

    final var hostToQuery = "dps-sample.dev";
    final var args = ConfigFlagArgsTemplates.withRandomPortsAndNotAsDefaultDns();
    final var app = new App(args);

    try (final var executor = Executors.newThreadExecutor()) {

      executor.submit(app::start);

      Threads.sleep(Duration.ofSeconds(2));

      final var port = app.getDnsServerPort();
      final var res = queryStartedServer(port, hostToQuery);
      assertTrue(Messages.isSuccess(res));

    }

  }

  @SneakyThrows
  static Message queryStartedServer(Integer port, String host) {
    final var dnsServerAddress = Ips.getAnyLocalAddress(port);
    final var dnsClient = new SimpleResolver(dnsServerAddress);
    return dnsClient.send(Messages.aQuestion(host));
  }
}
