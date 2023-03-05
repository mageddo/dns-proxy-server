package com.mageddo.windows;

import com.mageddo.commons.exec.CommandLines;
import com.mageddo.commons.exec.ExecutionValidationFailedException;
import com.mageddo.jna.ExecutionException;
import com.mageddo.windows.jna.IPHelperApi;
import com.mageddo.windows.jna.MIB_IFTABLE;
import com.sun.jna.ptr.IntByReference;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.mageddo.jna.WindowsConstants.ERROR_INSUFFICIENT_BUFFER;
import static com.mageddo.jna.WindowsConstants.NO_ERROR;

public class Networks {

  public static final String LINE_BREAK_REGEX = "\\r?\\n";

  private Networks() {
  }

  public static Pair<MIB_IFTABLE, Integer> findNetworkInterfaces0(int tableSizeInBytes) {
    final var ipHlpApi = IPHelperApi.INSTANCE;
    final var table = MIB_IFTABLE.fromBytesSize(tableSizeInBytes);
    final var inOutTableSize = new IntByReference(table.size());
    final int status = ipHlpApi.GetIfTable(table, inOutTableSize, false);
    if (status == NO_ERROR) {
      return Pair.of(table, null);
    } else if (status == ERROR_INSUFFICIENT_BUFFER) {
      return Pair.of(table, inOutTableSize.getValue() - 4);
    }
    throw new ExecutionException(status);
  }

  public static MIB_IFTABLE findNetworkInterfaces() {
    final var r = findNetworkInterfaces0(-1);
    if (r.getValue() == null) {
      return r.getKey();
    }
    final var r2 = findNetworkInterfaces0(r.getValue());
    Validate.isTrue(!Objects.equals(r2.getValue(), ERROR_INSUFFICIENT_BUFFER), "Can't get error insufficient twice");
    return r2.getKey();
  }


  public static List<String> findNetworksNames() {
    final var lines = CommandLines
      .exec("netsh interface ipv4 show interfaces")
      .checkExecution()
      .getOutAsString();
    System.out.println(lines);
//      .split(LINE_BREAK_REGEX);
    return null;
//    return Stream.of(lines)
//      .skip(1)
//      .filter(it -> !it.contains("*"))
//      .toList()
//      ;
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

    com.mageddo.dnsproxyserver.net.Networks.findInterfaces().forEach(it -> System.out.println(it.getName()));

    System.out.println("------------------");

    final var instance = com.mageddo.os.cross.Networks.getInstance();
    System.out.println(instance.findNetworks());
  }
}
