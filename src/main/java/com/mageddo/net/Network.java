package com.mageddo.net;

import com.mageddo.net.osx.NetworkOSX;
import com.mageddo.net.windows.NetworkWindows;
import com.mageddo.os.Platform;

import java.io.UncheckedIOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;
import java.util.stream.Stream;

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

  default Stream<NetworkInterface> findNetworkInterfaces(){
    try {
      return NetworkInterface.networkInterfaces();
    } catch (SocketException e) {
      throw new UncheckedIOException(e);
    }
  }

  static Network getInstance() {
    if (Platform.isMac()) {
      return new NetworkOSX();
    } else if (Platform.isWindows()) {
      return new NetworkWindows();
    } else if(Platform.isLinux()){
      return new NetworkLinux();
    }
    throw new UnsupportedOperationException("Unsupported platform: " + System.getProperty("os.name"));
  }

}
