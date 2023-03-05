package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import com.mageddo.os.osx.Networks;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class DnsConfiguratorCommandBased implements DnsConfigurator {

  private final Map<String, List<String>> serversBefore = new HashMap<>();

  /**
   * Do some validation before configure.
   */
  abstract void beforeConfigure(IpAddr addr) ;

  /**
   * List available networks
   * @return
   */
  abstract List<String> findNetworks();

  abstract boolean updateDnsServers(String network, List<String> servers);

  abstract List<String> findNetworkDnsServers(String network);


  @Override
  public void configure(IpAddr addr) {
    this.beforeConfigure(addr);
    for (final String network : this.findNetworks()) {
      final var serversBefore = this.findNetworkDnsServers(network);
      if (serversBefore != null) {
        this.serversBefore.put(network, serversBefore);
        final var success = this.updateDnsServers(network, Collections.singletonList(addr.getRawIP()));
        log.debug("status=configuring, network={}, serversBefore={}, success={}", network, serversBefore, success);
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

  Map<String, List<String>> getServersBefore() {
    return this.serversBefore;
  }
}
