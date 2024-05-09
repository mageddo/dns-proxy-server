package com.mageddo.dnsproxyserver.server.dns.solver.docker;

import com.mageddo.net.IP;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Value
@Builder
public class Container {

  @NonNull
  private String id;

  @NonNull
  private String name;

  @NonNull
  private Set<String> networkNames;

  @NonNull
  private Map<String, Network> networks;

  @NonNull
  private List<IP> ips;

  public IP getIp(IP.Version version) {
    throw new UnsupportedOperationException();
  }

  public IP getIp(IP.Version version, String networkName) {
    throw new UnsupportedOperationException();
  }

  @Value
  @Builder
  public static class Network {

    List<IP> ips;

    public String getIp(IP.Version version) {
      throw new UnsupportedOperationException();
    }
  }
}
