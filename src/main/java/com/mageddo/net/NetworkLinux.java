package com.mageddo.net;

import java.util.List;

public class NetworkLinux implements Network {

  @Override
  public List<String> findNetworks() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean updateDnsServers(String network, List<String> servers) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<String> findNetworkDnsServers(String network) {
    throw new UnsupportedOperationException();
  }
}
