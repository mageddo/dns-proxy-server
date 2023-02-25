package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.commons.lang.Objects;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.dnsconfigurator.DnsConfigurator;
import com.mageddo.dnsproxyserver.server.dns.IP;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class LinuxDnsConfigurator implements DnsConfigurator {

  private volatile AtomicReference<ResolvFile> confFile;

  @Override
  public void configure(IP ip) {

    this.init();
    if (this.confFile.get() == null) {
      return;
    }

    final var confFile = this.getConfFile();
    if (confFile.isResolvconf()) {
      ResolvconfConfigurator.process(confFile.getPath(), ip);
    } else if (confFile.isResolved()) {
      ResolvedConfigurator.configure(confFile.getPath(), ip);
    } else {
      throw newUnsupportedConfType(confFile);
    }
    log.debug("status=configured, path={}", this.getConfFile());
  }

  @Override
  public void restore() {
    this.init();
    if (this.confFile.get() == null) {
      return;
    }

    final var confFile = this.getConfFile();
    if (confFile.isResolvconf()) {
      ResolvconfConfigurator.restore(confFile.getPath());
    } else if (confFile.isResolved()) {
      ResolvedConfigurator.restore(confFile.getPath());
    } else {
      throw newUnsupportedConfType(confFile);
    }
    log.debug("status=restored, path={}", this.getConfFile());
  }

  ResolvFile getConfFile() {
    return this.confFile.get();
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

  List<Path> buildConfPaths() {
    return Objects.firstNonNull(
      splitToPaths(getConfigResolvPaths()),
      Collections.emptyList()
    );
  }

  String getConfigResolvPaths() {
    return Configs.getInstance()
      .getResolvConfPaths()
      ;
  }

  ResolvFile toResolvFile(Path path) {
    return ResolvFile.of(path, LinuxResolverConfDetector.detect(path));
  }

  void init() {
    if (this.confFile == null) {
      this.confFile = new AtomicReference<>(findBestConfFile());
      log.info("status=using, configFile={}", this.getConfFile());
    }
  }

  private RuntimeException newUnsupportedConfType(ResolvFile confFile) {
    return new UnsupportedOperationException(String.format("conf file not supported: %s", confFile));
  }

}
