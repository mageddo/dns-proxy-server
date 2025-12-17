package com.mageddo.net.windows;

import java.util.List;

import com.mageddo.commons.lang.Objects;
import com.mageddo.jna.net.windows.registry.NetworkRegistry;
import com.mageddo.net.Network;
import com.mageddo.net.windows.registry.NetworkInterface;

import static com.mageddo.jna.net.windows.registry.NetworkRegistry.findNetworkInterfaceOrNull;

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
    return Objects.mapOrNull(findNetworkInterfaceOrNull(network),
        NetworkInterface::getStaticDnsServers
    );
  }
}
