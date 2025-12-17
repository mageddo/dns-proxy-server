package com.mageddo.net.osx;

import java.util.List;

import com.mageddo.net.Network;

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
    return Networks.findNetworkDnsServersOrEmpty(network);
  }
}
