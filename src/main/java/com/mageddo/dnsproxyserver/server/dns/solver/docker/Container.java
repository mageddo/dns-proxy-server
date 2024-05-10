package com.mageddo.dnsproxyserver.server.dns.solver.docker;

import com.mageddo.net.IP;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Value
@Builder
public class Container {

  @NonNull
  private String id;

  @NonNull
  private String name;

  @NonNull
  private Set<String> preferredNetworkNames;

  @NonNull
  private Map<String, Network> networks;

  @NonNull
  private List<IP> ips;

  public IP geDefaultIp(IP.Version version) {
    return this.ips.stream()
      .filter(it -> Objects.equals(it.version(), version))
      .findFirst()
      .orElse(null);
  }

  public IP geDefaultIp(IP.Version version, String networkName) {
    throw new UnsupportedOperationException();
  }

  public String getFirstNetworkName() {
    return this.preferredNetworkNames
      .stream()
      .findFirst()
      .orElse(null)
      ;
  }

  @Value
  @Builder
  public static class Network {

    List<IP> ips;

    public String getIpAsText(IP.Version version) {
      return this.ips.stream()
        .filter(it -> Objects.equals(it.version(), version))
        .findFirst()
        .map(IP::toText)
        .orElse(null);
    }
  }
}
