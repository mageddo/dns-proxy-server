package com.mageddo.dnsproxyserver.dnsconfigurator.linux;

import com.mageddo.commons.lang.Objects;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.dnsconfigurator.DnsConfigurator;
import com.mageddo.dnsproxyserver.dnsconfigurator.linux.ResolvFile.Type;
import com.mageddo.dnsproxyserver.systemd.ResolvedService;
import com.mageddo.io.path.predicate.PathExistsPredicate;
import com.mageddo.io.path.predicate.PathIsFilePredicate;
import com.mageddo.io.path.predicate.PathIsWritablePredicate;
import com.mageddo.net.IpAddr;
import com.mageddo.utils.Tests;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static com.mageddo.dnsproxyserver.utils.Splits.splitToPaths;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

@Slf4j
@Default
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DnsConfiguratorLinux implements DnsConfigurator {

  private final AtomicBoolean resolvedConfigured = new AtomicBoolean();

  private volatile AtomicReference<ResolvFile> confFile;
  private final List<Predicate<Path>> pathPredicates = List.of(
    new PathExistsPredicate(),
    new PathIsFilePredicate(),
    new PathIsWritablePredicate()
  );

  @Override
  public void configure(IpAddr addr) {

    this.init();
    if (this.confFile.get() == null) {
      return;
    }

    final var confFile = this.getConfFile();

    if (confFile.isResolvconf()) {
      final var overrideNameServers = this.isOverrideNameServersActive();
      ResolvconfConfigurator.process(confFile.getPath(), addr, overrideNameServers);
    } else if (confFile.isResolved()) {
      this.configureResolved(addr, confFile);
    } else {
      throw newUnsupportedConfType(confFile);
    }
    log.debug("status=configured, path={}", this.getConfFile());
  }

  private boolean isOverrideNameServersActive() {
    return Configs
      .getInstance()
      .isResolvConfOverrideNameServersActive();
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
      tryRestartResolved();
    } else {
      throw newUnsupportedConfType(confFile);
    }
    log.debug("status=restored, path={}", this.getConfFile());
  }

  ResolvFile getConfFile() {
    return this.confFile.get();
  }

  ResolvFile findBestConfFile() {
    return this.buildConfPaths()
      .stream()
      .filter(it -> {
          final var unmetPredicate = this.findUnmetPredicate(it);
          unmetPredicate.ifPresent(pathPredicate -> {
            log.info("status=noValidConfFile, file={}, condition={}", it, ClassUtils.getSimpleName(pathPredicate));
          });
          return unmetPredicate.isEmpty();
        }
      )
      .map(this::toResolvFile)
      .findFirst()
      .orElse(null)
      ;
  }

  private Optional<Predicate<Path>> findUnmetPredicate(Path it) {
    return this.pathPredicates
      .stream()
      .filter(p -> !p.test(it))
      .findFirst();
  }

  List<Path> buildConfPaths() {
    return Objects.firstNonNull(
      splitToPaths(getConfigResolvPaths()),
      Collections.emptyList()
    );
  }

  String getConfigResolvPaths() {
    return Configs.getInstance()
      .getDefaultDnsResolvConfPaths()
      ;
  }

  ResolvFile toResolvFile(Path path) {
    return ResolvFile.of(path, firstNonNull(LinuxResolverConfDetector.detect(path), Type.RESOLVCONF));
  }

  void init() {
    if (this.confFile == null) {
      this.confFile = new AtomicReference<>(this.findBestConfFile());
      log.info("status=using, configFile={}", this.getConfFile());
    }
  }

  private RuntimeException newUnsupportedConfType(ResolvFile confFile) {
    return new UnsupportedOperationException(String.format("conf file not supported: %s", confFile));
  }

  private void configureResolved(IpAddr addr, ResolvFile confFile) {
    if (this.resolvedConfigured.compareAndSet(false, true)) {
      ResolvedConfigurator.configure(confFile.getPath(), addr);
      tryRestartResolved();
    }
  }

  static void tryRestartResolved() {
    try {
      if (Tests.inTest()) {
        log.warn("status=wont-restart-service-while-testing");
        return;
      }
      ResolvedService.restart();
    } catch (Throwable e) {
      log.warn(
        "status=can't restart resolved service, please run: "
        + "'service systemd-resolved restart' to apply DPS as default DNS.\n{}",
        e.getMessage()
      );
    }
  }

}
