package com.mageddo.net.windows;

import com.mageddo.commons.lang.Objects;
import com.mageddo.net.Network;
import com.mageddo.net.windows.registry.NetworkInterface;
import com.mageddo.net.windows.registry.NetworkRegistry;

import java.util.List;

import static com.mageddo.net.windows.registry.NetworkRegistry.findNetworkInterface;

public class NetworkWindows implements Network {

  @Override
  public List<String> findNetworks() {
    return NetworkRegistry.findNetworksWithIpIds();
  }

  @Override
  public boolean updateDnsServers(String network, List<String> servers) {
    NetworkRegistry.updateDnsServer(network, servers);
    return true;
  }

  @Override
  public List<String> findNetworkDnsServers(String network) {
    return Objects.mapOrNull(findNetworkInterface(network), NetworkInterface::getStaticDnsServers);
  }
}
