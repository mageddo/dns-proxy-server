package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.solver.SimpleResolver;
import com.mageddo.net.IpAddr;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;

import java.io.IOException;

@Slf4j
public class DnsServerHealthChecker implements HealthChecker {

  private final SimpleResolver resolver;

  public DnsServerHealthChecker(IpAddr addr) {
    this.resolver = new SimpleResolver(addr);
  }

  @Override
  public boolean isHealthy() {
    final var req = Messages.aQuestion("dps.dns.test");
    try {
      final var res = this.resolver.send(req);
      log.debug("status=done, server={}, res={}", this.resolver.getAddress(), Messages.simplePrint(res));
      return Messages.findQuestionTypeCode(res) != null;
    } catch (IOException e) {
      log.debug(
        "status=failed, server={}, res={}, clazz={}",
        this.resolver.getAddress(), e.getMessage(), ClassUtils.getSimpleName(e)
      );
      return false;
    }
  }

  @Override
  public String toString() {
    return String.valueOf(this.resolver.getAddress());
  }
}
