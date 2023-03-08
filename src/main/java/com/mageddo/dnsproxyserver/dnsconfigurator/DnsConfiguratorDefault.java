package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import com.mageddo.dnsproxyserver.utils.Dns;
import com.mageddo.net.Network;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DnsConfiguratorDefault implements DnsConfigurator {

  private final Map<String, List<String>> serversBefore = new HashMap<>();
  private final Network delegate = Network.getInstance();

  @Override
  public void configure(IpAddr addr) {
    Dns.validateIsDefaultPort(addr);
    for (final String network : this.findNetworks()) {
      if (!this.serversBefore.containsKey(network)) {
        final var serversBefore = this.findNetworkDnsServers(network);
        this.serversBefore.put(network, serversBefore);
        final var success = this.updateDnsServers(network, Collections.singletonList(addr.getRawIP()));
        log.debug("status=configuring, network={}, serversBefore={}, success={}", network, this.serversBefore, success);
      } else {
        log.debug("status=alreadyConfigured, network={}", network);
      }
    }
  }

  @Override
  public void restore() {
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
}
