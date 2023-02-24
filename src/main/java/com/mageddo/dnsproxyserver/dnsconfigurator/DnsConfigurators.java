package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.commons.concurrent.ThreadPool;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.dnsconfigurator.linux.LinuxDnsConfigurator;
import com.mageddo.dnsproxyserver.server.dns.IP;
import io.quarkus.runtime.StartupEvent;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.OS;
import org.apache.commons.lang3.ClassUtils;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DnsConfigurators {

  private final LinuxDnsConfigurator linuxConfigurator;
  private final DpsIpDiscover ipDiscover;

  private volatile DnsConfigurator instance;

  void onStart(@Observes StartupEvent ev) {
    final var config = Configs.getInstance();
    log.debug("action=setAsDefaultDns, active={}", config.getDefaultDns());
    if (!Boolean.TRUE.equals(config.getDefaultDns())) {
      return;
    }

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      log.debug("status=restoringResolvConf, path={}", config.getResolvConfPath());
      this.getInstance().restore();
    }));

    ThreadPool
      .def()
      .scheduleWithFixedDelay(() -> {
        try {
          this.getInstance().configure(this.ipDiscover.findDpsIP());
        } catch (Exception e) {
          if (e instanceof IOException) {
            log.warn(
              "status=failedToConfigureAsDefaultDns, path={}, msg={}:{}",
              config.getResolvConfPath(), ClassUtils.getName(e), e.getMessage()
            );
          } else {
            log.warn("status=failedToConfigureAsDefaultDns, path={}, msg={}", config.getResolvConfPath(), e.getMessage(), e);
          }
        }
      }, 5, 20, TimeUnit.SECONDS);
  }

  DnsConfigurator getInstance() {
    return this.instance != null ? this.instance : (this.instance = getInstance0());
  }

  private DnsConfigurator getInstance0() {
    if (OS.isFamilyUnix() && !OS.isFamilyMac()) {
      return this.linuxConfigurator;
    }
    log.info("status=unsupported-platform-to-set-as-default-dns-automatically, os={}", System.getProperty("os.name"));
    return new DnsConfigurator() {
      public void configure(IP ip) {
      }

      public void restore() {
      }
    };
  }
}
