package com.mageddo.net.windows;

import com.mageddo.commons.exec.CommandLines;
import com.mageddo.commons.exec.ExecutionValidationFailedException;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class Networks {

  public static final String LINE_BREAK_REGEX = "\\r?\\n";

  private Networks() {
  }

  public static List<String> findNetworksNames() {
//    return Stream
//      .of(IpHelper.findNetworkInterfaces().table)
//      .map(it -> new String(Hex.encodeHexString(it.bPhysAddr)) + " : "+ new String(it.wszName) + new String(it.bDescr))
//      .toList();
    throw new UnsupportedOperationException();
  }

  public static List<String> findNetworkDnsServers(String networkServiceName) {
    final var out = CommandLines
      .exec("networksetup -getdnsservers \"%s\"", networkServiceName)
      .checkExecution()
      .getOutAsString();
    if (out.contains(networkServiceName)) { // probably "any DNS Servers set on" but can filter that as language can change
      return Collections.emptyList();
    }
    return Stream
      .of(out.split(LINE_BREAK_REGEX))
      .filter(StringUtils::isNotBlank)
      .toList();
  }

  /**
   * @return the configured dns servers or null when network service is disabled or not available.
   */
  public static List<String> findNetworkDnsServersOrNull(String networkName) {
    try {
      return findNetworkDnsServers(networkName);
    } catch (ExecutionValidationFailedException e) {
      if (isUnrecognizedNetwork(networkName, e)) { // probably is not a recognized network service
        return null;
      }
      throw e;
    }
  }

  public static boolean clearDns(String networkName) {
    return updateDnsServers(networkName, "Empty");
  }

  public static boolean updateDnsServers(String networkName, String... dnsServers) {
    final var serversParam = String.join(" ", dnsServers);
    try {
      CommandLines
        .exec("networksetup -setdnsservers \"%s\" %s", networkName, serversParam)
        .checkExecution();
      return true;
    } catch (ExecutionValidationFailedException e) {
      if (isUnrecognizedNetwork(networkName, e)) {
        return false;
      }
      throw e;
    }
  }

  static boolean isUnrecognizedNetwork(String networkName, ExecutionValidationFailedException e) {
    return e.result().getExitCode() == 4 && e.getMessage().contains(networkName);
  }

  public static boolean updateDnsServers(String network, List<String> servers) {
    if (servers.isEmpty()) {
      return com.mageddo.os.osx.Networks.clearDns(network);
    } else {
      return updateDnsServers(network, servers.toArray(new String[0]));
    }
  }

  public static void main(String[] args) {

    com.mageddo.net.Networks.findInterfaces().forEach(it -> System.out.println(it.getDisplayName()));

    System.out.println("------------------");

    final var instance = com.mageddo.os.cross.Networks.getInstance();
    System.out.println(instance.findNetworks());
  }
}
