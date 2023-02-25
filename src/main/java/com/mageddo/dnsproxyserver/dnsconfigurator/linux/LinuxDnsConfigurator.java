package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.commons.lang.Objects;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.dnsconfigurator.DnsConfigurator;
import com.mageddo.dnsproxyserver.dnsconfigurator.linux.resolvconf.DnsServerCleanerHandler;
import com.mageddo.dnsproxyserver.dnsconfigurator.linux.resolvconf.SetMachineDNSServerHandler;
import com.mageddo.dnsproxyserver.resolvconf.ResolvConfParser;
import com.mageddo.dnsproxyserver.server.dns.IP;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.mageddo.dnsproxyserver.utils.Splits.splitToPaths;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class LinuxDnsConfigurator implements DnsConfigurator {

  private AtomicReference<ResolvFile> confFile;

  @Override
  public void configure(IP ip) {

    if (this.confFile == null) {
      this.confFile = new AtomicReference<>(findBestConfFile());
      log.info("status=using, configFile={}", this.getConfFile());
    }
    if (this.confFile.get() == null) {
      return;
    }

    ResolvConfParser.process(getConfFile(), new SetMachineDNSServerHandler(ip.raw()));
  }

  @Override
  public void restore() {
    if (this.confFile.get() == null) {
      return;
    }
    ResolvConfParser.process(getConfFile(), new DnsServerCleanerHandler());
    log.debug("status=restoredResolvConf, path={}", this.getConfFile());
  }

  Path getConfFile() {
    return this.confFile.get().getPath();
  }

  ResolvFile findBestConfFile() {
    return buildConfPaths()
      .stream()
      .filter(it -> {
          final var valid = Files.exists(it)
            && !Files.isDirectory(it)
            && Files.isWritable(it);
          if (!valid) {
            log.info("status=noValidConfFile, file={}", it);
          }
          return valid;
        }
      )
      .map(this::toResolvFile)
      .findFirst()
      .orElse(null)
      ;
  }

  ResolvFile toResolvFile(Path path) {
    throw new UnsupportedOperationException();
  }

  List<Path> buildConfPaths() {
    return Objects.firstNonNull(
      splitToPaths(Configs.getInstance().getResolvConfPaths()),
      Collections.emptyList()
    );
  }
}
