package com.mageddo.dnsproxyserver;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.quarkus.QuarkusConfig;
import com.mageddo.dnsproxyserver.server.dns.ServerStarter;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.QuarkusMain;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.event.Observes;

import static com.mageddo.dnsproxyserver.quarkus.Quarkus.isTest;

@Slf4j
@QuarkusMain
public class App {
  public static void main(String[] args) {

    // configurations
    final var config = Configs.buildAndRegister(args);

    // setup quarkus configs
    QuarkusConfig.setup(config);

    // todo setup as default dns

    // todo install as service

    // start webserver
    // start dns server
    Quarkus.run(args);

  }

  void onStart(@Observes StartupEvent ev, ServerStarter dnsServer) {
    if(isTest()){
      log.warn("status=won't-start-dns-server-when-testing");
      return;
    } else {
      dnsServer.start();
    }
  }
}
