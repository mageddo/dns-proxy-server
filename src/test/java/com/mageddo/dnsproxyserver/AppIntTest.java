package com.mageddo.dnsproxyserver;

import com.mageddo.commons.concurrent.Threads;
import com.mageddo.commons.exec.ProcessesWatchDog;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.config.dataprovider.JsonConfigs;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJson;
import com.mageddo.dnsproxyserver.sandbox.Instance;
import com.mageddo.dnsproxyserver.sandbox.Sandbox;
import com.mageddo.dnsproxyserver.server.Starter;
import com.mageddo.dnsproxyserver.solver.SimpleResolver;
import com.mageddo.dnsproxyserver.utils.Ips;
import com.mageddo.net.IpAddr;
import com.mageddo.utils.Executors;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xbill.DNS.Message;
import testing.templates.ConfigFlagArgsTemplates;
import testing.templates.ConfigJsonFileTemplates;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class AppIntTest {

  @BeforeEach
  void beforeEach() {
    Starter.setMustStartFlagActive(true);
    Configs.clear();
  }

  @AfterAll
  static void afterAll() {
    Starter.setMustStartFlagActive(false);
    ProcessesWatchDog.instance().killAllProcesses();
  }

  @Test
  void appMustStartAndQuerySampleWithSuccessFromLocalDbSolver() {

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

  @Test
  void mustQueryRemoteSolverPassingThroughAllModulesAndGetSuccess() {

    final var hostToQuery = "dps-int-test.dev";

    try (final var executor = Executors.newThreadExecutor()) {

      final var serverAppConfig = buildAndStartServerApp(hostToQuery);
      final var clientApp = buildClientAppAndWait(executor, serverAppConfig.getDnsServerPort());

      final var port = clientApp.getDnsServerPort();
      final var res = queryStartedServer(port, hostToQuery);

      assertTrue(Messages.isSuccess(res));
      assertEquals("192.168.0.1", Messages.findAnswerRawIP(res));

    }

  }

  private static App buildClientAppAndWait(ExecutorService executor, Integer serverPort) {
    final var remoteAddr = IpAddr.of("127.0.0.1", serverPort);
    return buildAppAndWait(executor, ConfigFlagArgsTemplates.withRandomPortsAndNotAsDefaultDnsUsingRemote(remoteAddr));
  }

  private static Result buildAndStartServerApp(String hostToQuery) {
    final var configFile = ConfigJsonFileTemplates.withRandomPortsAndNotAsDefaultDnsAndCustomLocalDBEntry(hostToQuery);
    final var instance = Sandbox.runFromGradleTests(configFile);
    return Result.of(configFile, instance);
  }

  private static App buildAppAndWait(ExecutorService executor, final String[] params) {
    log.debug("app={}", Arrays.toString(params));
    final var app = new App(params);
    executor.submit(app::start);
    Threads.sleep(Duration.ofSeconds(2));
    return app;
  }

  @SneakyThrows
  static Message queryStartedServer(Integer port, String host) {
    final var dnsServerAddress = Ips.getAnyLocalAddress(port);
    final var dnsClient = new SimpleResolver(dnsServerAddress);
    return dnsClient.send(Messages.aQuestion(host));
  }

  @Value
  static class Result {

    private final ConfigJson config;
    private final Instance instance;

    public static Result of(Path configFile, Instance instance) {
      return new Result(JsonConfigs.loadConfig(configFile), instance);
    }

    public Integer getDnsServerPort() {
      return this.config.getDnsServerPort();
    }

  }
}
