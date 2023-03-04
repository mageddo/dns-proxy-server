package com.mageddo.os.osx;

import com.mageddo.commons.exec.CommandLines;
import com.mageddo.commons.exec.ExecutionValidationFailedException;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class Networks {

  public static final String LINE_BREAK_REGEX = "\\r?\\n";

  public static List<String> findNetworksNames() {
    final var lines = CommandLines
      .exec("networksetup -listallnetworkservices")
      .checkExecution()
      .getOutAsString()
      .split(LINE_BREAK_REGEX);

    return Stream.of(lines)
      .skip(1)
      .filter(it -> !it.contains("*"))
      .toList()
      ;
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
      if (e.result().getExitCode() == 4 && e.getMessage().contains(networkName)) { // probably is not a recognized network service
        return null;
      }
      throw e;
    }
  }

  public static void main(String[] args) {
    System.out.println("Networks: " + findNetworksNames());
    for (String networkName : findNetworksNames()) {
      try {
        System.out.printf("n=%s, dns=%s%n", networkName, findNetworkDnsServersOrNull(networkName));
      } catch (Exception e) {
        System.out.println(ClassUtils.getSimpleName(e) + ": " + e.getMessage());
      }
    }
  }
}
