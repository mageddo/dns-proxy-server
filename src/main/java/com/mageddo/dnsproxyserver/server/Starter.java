package com.mageddo.dnsproxyserver.server;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.di.StartupEvent;
import com.mageddo.dnsproxyserver.server.dns.ServerStarter;
import com.mageddo.http.WebServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.Set;

import static com.mageddo.dnsproxyserver.quarkus.Quarkus.isTest;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class Starter {

  private final ServerStarter dnsServerStarter;
  private final WebServer webServer;
  private final Set<StartupEvent> startupEvents;

  public void start() {
    if(isTest()){
      log.warn("status=onTest, disabled=[dnsServer, startupEvents]");
    } else {
      this.startupEvents.forEach(StartupEvent::onStart);
      this.dnsServerStarter.start();
    }
    this.webServer.start(Configs.getInstance().getWebServerPort());
  }

  public void stop(){
    this.dnsServerStarter.stop();
    this.webServer.stop();
  }
}
