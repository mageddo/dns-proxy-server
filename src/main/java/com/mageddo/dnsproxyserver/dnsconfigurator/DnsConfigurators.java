package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.commons.concurrent.ThreadPool;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.dnsconfigurator.linux.DnsConfiguratorLinux;
import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import io.quarkus.runtime.StartupEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.OS;
import org.apache.commons.lang3.ClassUtils;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DnsConfigurators {

  private final DnsConfiguratorLinux linuxConfigurator;
  private final DnsConfiguratorOSx osxConfigurator;
  private final DpsIpDiscover ipDiscover;
  private final AtomicInteger failures = new AtomicInteger();

  private volatile DnsConfigurator instance;

  void onStart(@Observes StartupEvent ev) {
    final var config = Configs.getInstance();
    log.debug("action=setAsDefaultDns, active={}", config.getDefaultDns());
    if (!Boolean.TRUE.equals(config.getDefaultDns())) {
      return;
    }

    this.configureShutdownHook(config);
    this.configurationHook(config);
  }

  private void configurationHook(Config config) {
    ThreadPool
      .def()
      .scheduleWithFixedDelay(() -> {
        try {
          final var addr = this.findIpAddr();
          log.trace("status=configuringAsDefaultDns, addr={}", addr);
          this.configure(addr);
        } catch (Throwable e) {
          this.failures.incrementAndGet();
          if (e instanceof IOException) {
            log.warn(
              "status=failedToConfigureAsDefaultDns, path={}, msg={}:{}",
              config.getResolvConfPaths(), ClassUtils.getName(e), e.getMessage()
            );
          } else {
            log.warn("status=failedToConfigureAsDefaultDns, path={}, msg={}", config.getResolvConfPaths(), e.getMessage(), e);
          }
          if (this.failures.get() >= this.getMaxErrors()) {
            log.warn("status=too-many-failures, action=stopping-default-dns-configuration, failures={}", this.failures.get());
            throw new RuntimeException(e);
          }
        }
      }, this.getInitialDelay(), this.getDelay(), TimeUnit.MILLISECONDS);
  }

  IpAddr findIpAddr() {
    return IpAddr.of(
      this.ipDiscover.findDpsIP(),
      Configs.getInstance().getDnsServerPort()
    );
  }

  int getDelay() {
    return 20_000;
  }

  int getInitialDelay() {
    return 5_000;
  }

  int getMaxErrors() {
    return 3;
  }

  int getFailures() {
    return failures.get();
  }

  void configureShutdownHook(Config config) {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      log.debug("status=restoringResolvConf, path={}", config.getResolvConfPaths());
      this.getInstance().restore();
    }));
  }

  void configure(IpAddr addr) {
    this.getInstance().configure(addr);
  }

  DnsConfigurator getInstance() {
    return this.instance != null ? this.instance : (this.instance = getInstance0());
  }

  private DnsConfigurator getInstance0() {
    if (OS.isFamilyMac()) {
      return this.osxConfigurator;
    } else if (OS.isFamilyUnix()) {
      return this.linuxConfigurator;
    }
    log.info("status=unsupported-platform-to-set-as-default-dns-automatically, os={}", System.getProperty("os.name"));
    return new DnsConfigurator() {
      public void configure(IpAddr addr) {
      }

      public void restore() {
      }
    };
  }

}
