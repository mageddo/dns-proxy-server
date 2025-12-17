package com.mageddo.dnsproxyserver.dnsconfigurator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.utils.Dns;
import com.mageddo.net.IpAddr;
import com.mageddo.net.Network;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class DnsConfiguratorDefault implements DnsConfigurator {

  private final Map<String, List<String>> serversBefore;
  private final Network delegate;
  private RuntimeException error;

  @Inject
  public DnsConfiguratorDefault() {
    this.serversBefore = new HashMap<>();
    this.delegate = createInstance();
  }

  @Override
  public void configure(IpAddr addr) {
    this.validatePlatformIsSupported();
    Dns.validateIsDefaultPort(addr);
    for (final String network : this.findNetworks()) {
      if (!this.serversBefore.containsKey(network)) {
        final var serversBefore = this.findNetworkDnsServers(network);
        this.serversBefore.put(network, serversBefore);
        final var success = this.updateDnsServers(network,
            Collections.singletonList(addr.getRawIP())
        );
        log.debug("status=configuring, network={}, serversBefore={}, success={}", network,
            this.serversBefore, success
        );
      } else {
        log.debug("status=alreadyConfigured, network={}", network);
      }
    }
  }

  @Override
  public void restore() {
    this.validatePlatformIsSupported();
    log.info("status=restoringPreviousDnsServers...");
    this.serversBefore.forEach((network, servers) -> {
      final var success = this.updateDnsServers(network, servers);
      log.info("status=restoring, network={}, servers={}, success={}", network, servers, success);
    });
  }

  boolean updateDnsServers(String network, List<String> servers) {
    return this.delegate.updateDnsServers(network, servers);
  }

  List<String> findNetworkDnsServers(String network) {
    return this.delegate.findNetworkDnsServers(network);
  }

  List<String> findNetworks() {
    return this.delegate.findNetworks();
  }

  Map<String, List<String>> getServersBefore() {
    return this.serversBefore;
  }

  Network createInstance() {
    try {
      return Network.getInstance();
    } catch (UnsupportedOperationException e) {
      this.error = e;
      return null;
    }
  }

  void validatePlatformIsSupported() {
    if (this.error != null) {
      throw this.error;
    }
  }

}
