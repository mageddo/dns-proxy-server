package com.mageddo.net;

import com.mageddo.net.osx.NetworkOSX;
import com.mageddo.net.windows.NetworkWindows;
import com.sun.jna.Platform;

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

  static Network getInstance() {
    if (Platform.isMac()) {
      return new NetworkOSX();
    } else if (Platform.isWindows()) {
      return new NetworkWindows();
    }
    throw new UnsupportedOperationException("Unsupported platform: " + System.getProperty("os.name"));
  }
}
