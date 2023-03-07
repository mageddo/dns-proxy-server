package com.mageddo.os.cross;

import com.mageddo.net.windows.Networks;

import java.util.List;

public class NetworkWindows implements Network {

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
    return Networks.findNetworkDnsServers(network);
  }
}
