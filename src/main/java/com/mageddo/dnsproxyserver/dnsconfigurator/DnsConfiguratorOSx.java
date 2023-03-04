package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import com.mageddo.dnsproxyserver.utils.DNS;
import com.mageddo.os.osx.Networks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DnsConfiguratorOSx implements DnsConfigurator {

  private final Map<String, List<String>> serversBefore = new HashMap<>();

  @Override
  public void configure(IpAddr addr) {
    DNS.validateIsDefaultPort(addr);
    for (final String network : Networks.findNetworksNames()) {
      final var serversBefore = Networks.findNetworkDnsServers(network);
      if (serversBefore != null) {
        this.serversBefore.put(network, serversBefore);
        final var success = Networks.updateDnsServers(network, addr.getRawIP());
        log.debug("status=configuring, network={}, serversBefore={}, success={}", network, serversBefore, success);
      }
    }
  }

  @Override
  public void restore() {
    this.serversBefore.forEach((network, servers) -> {
      final var success = Networks.updateDnsServers(network, servers);
      log.debug("status=restoring, network={}, servers={}, success={}", network, servers, success);
    });
  }
}
