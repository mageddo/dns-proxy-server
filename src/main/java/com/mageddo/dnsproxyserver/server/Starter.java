package com.mageddo.dnsproxyserver.server;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.di.StartupEvent;
import com.mageddo.dnsproxyserver.server.dns.ServerStarter;
import com.mageddo.http.WebServer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.mageddo.dnsproxyserver.quarkus.Quarkus.isTest;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Starter {

  public static final String DNS_SERVER_MUST_START_FLAG = "mg.server.server.must-start";
  private final ServerStarter dnsServerStarter;
  private final WebServer webServer;
  private final Set<StartupEvent> startupEvents;

  public void start() {
    if (isTest()) {
      log.warn("status=onTest, disabled=[startupEvents]");
    } else {
      this.startupEvents.forEach(StartupEvent::onStart);
    }
    if (shouldStartDnsServer()) {
      this.startDnsServer();
    }
    this.startWebServer();
  }

  void startWebServer() {
    this.webServer.start(Configs.getInstance()
        .getWebServerPort());
  }

  void startDnsServer() {
    this.dnsServerStarter.start();
  }

  private static boolean shouldStartDnsServer() {
    return !isTest() || isMustStartFlagActive();
  }

  private static boolean isMustStartFlagActive() {
    return Boolean.getBoolean(DNS_SERVER_MUST_START_FLAG);
  }

  public static void setMustStartFlagActive(boolean b) {
    System.setProperty(DNS_SERVER_MUST_START_FLAG, String.valueOf(b));
  }

  public void stop() {
    this.dnsServerStarter.stop();
    this.webServer.stop();
  }
}
