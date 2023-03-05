package com.mageddo.os.cross;

import java.util.List;

public interface Network {

  /**
   * @return available networks for the current OS.
   */
  List<String> findNetworks();

  /**
   * Set the servers as current DNS for the specified network.
   * @return whether had success.
   */
  boolean updateDnsServers(String network, List<String> servers);

  /**
   * Find current configured DNS for the specified network, it may not return anything in cases the
   * Network is using a DNS provided the Router or Modem.
   */
  List<String> findNetworkDnsServers(String network);
}
