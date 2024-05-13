package com.mageddo.dnsproxyserver.server.dns.solver.docker;

import com.mageddo.net.IP;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
  @Builder.Default
  private Map<String, Network> networks = Collections.emptyMap();

  @NonNull
  @Builder.Default
  private List<IP> ips = Collections.emptyList();

  public IP geDefaultIp(IP.Version version) {
    return this.ips.stream()
      .filter(it -> Objects.equals(it.version(), version))
      .findFirst()
      .orElse(null);
  }

  public IP getNetworkIp(IP.Version version, String networkName) {
    return this.getNetworkOptional(networkName)
      .map(it -> it.getIp(version))
      .orElse(null);
  }

  public String getFirstNetworkName() {
    return this.preferredNetworkNames
      .stream()
      .findFirst()
      .orElse(null)
      ;
  }

  public Set<String> getNetworksNames() {
    return this.networks.keySet();
  }

  public Network getNetwork(String name) {
    return this.networks.get(name);
  }

  public Optional<Network> getNetworkOptional(String name) {
    return Optional.ofNullable(this.getNetwork(name));
  }

  @Value
  @Builder
  public static class Network {

    List<IP> ips;

    public String getIpAsText(IP.Version version) {
      return Optional.ofNullable(this.getIp(version))
        .map(IP::toText)
        .orElse(null);
    }

    public IP getIp(IP.Version version) {
      return this.ips.stream()
        .filter(it -> Objects.equals(it.version(), version))
        .findFirst()
        .orElse(null);
    }

  }
}
