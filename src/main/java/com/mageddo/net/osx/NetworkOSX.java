package com.mageddo.net.osx;

import com.mageddo.net.Network;

import java.util.List;

public class NetworkOSX implements Network {
  @Override
  public List<String> findNetworks() {
    return Networks.findNetworksNames();
  }

  @Override
  public boolean updateDnsServers(String network, List<String> servers) {
    return Networks.updateDnsServers(network, servers);
  }

  @Override
  public List<String> findNetworkDnsServers(String network) {
    return Networks.findNetworkDnsServersOrNull(network);
  }
}
