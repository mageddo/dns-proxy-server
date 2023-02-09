package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.threads.ThreadPool;
import io.quarkus.runtime.StartupEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.OS;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class DnsConfigurators {

  private final LinuxDnsConfigurator linuxConfigurator;
  private final DpsIpDiscover ipDiscover;

  void onStart(@Observes StartupEvent ev) {
    final var config = Configs.getInstance();
    log.debug("action=setAsDefaultDns, active={}", config.getDefaultDns());
    if (!Boolean.TRUE.equals(config.getDefaultDns())) {
      return;
    }
    ThreadPool
      .main()
      .scheduleWithFixedDelay(() -> {
        try {
          this.linuxConfigurator.configure(this.ipDiscover.findDpsIP(), config.getResolvConfPath());
        } catch (Exception e) {
          log.warn("status=failedToConfigureAsDefaultDns, path={}, msg={}", config.getResolvConfPath(), e.getMessage(), e);
        }

      }, 5, 20, TimeUnit.SECONDS);
  }

  public DnsConfigurator getInstance() {
    if (OS.isFamilyUnix() && !OS.isFamilyMac()) {
      return this.linuxConfigurator;
    }
    log.debug("status=unsupported-platform-to-set-as-default-dns-automatically, os={}", System.getProperty("os.name"));
    return null;
  }
}
