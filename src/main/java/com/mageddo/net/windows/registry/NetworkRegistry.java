package com.mageddo.net.windows.registry;

import com.mageddo.jna.Exceptions;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinReg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sun.jna.platform.win32.Advapi32Util.registryGetStringArray;
import static com.sun.jna.platform.win32.Advapi32Util.registryGetStringValue;
import static com.sun.jna.platform.win32.Advapi32Util.registrySetStringValue;

@Slf4j
public class NetworkRegistry {

  public static final String DNS_SERVER_ATTR = "NameServer";
  public static final WinReg.HKEY HKEY_LOCAL_MACHINE = WinReg.HKEY_LOCAL_MACHINE;

  /**
   * @param networkId something like {ab01ba7a-f236-4f47-933f-46b48affecd4}
   * @see https://stackoverflow.com/a/17819465/2979435
   * https://stackoverflow.com/questions/62289/read-write-to-windows-registry-using-java
   */
  public static List<String> findStaticDnsServers(String networkId) {
    final String str = findNetworkStringValue(networkId, DNS_SERVER_ATTR);
    return Stream
      .of(str.split(","))
      .toList();
  }

  public static List<String> findNetworksWithIpIds() {
    return NetworkRegistry
      .findNetworksWithIp()
      .stream()
      .map(NetworkInterface::getId)
      .toList()
      ;
  }

  public static Set<String> findNetworksIds() {
    return Stream.of(Advapi32Util.registryGetKeys(
        HKEY_LOCAL_MACHINE,
        "SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters\\Interfaces"
      ))
      .collect(Collectors.toSet());
  }

  public static List<NetworkInterface> findNetworksWithIp() {
    return findNetworksIds()
      .stream()
      .map(NetworkRegistry::findNetworkInterface)
      .filter(NetworkInterface::hasIp)
      .toList()
      ;
  }

  public static NetworkInterface findNetworkInterface(String networkId) {
    try {
      return NetworkInterface.builder()
        .id(networkId)
        .staticIp(findIpAddress(networkId))
        .dhcpIp(findDhcpIpAddress(networkId))
        .staticDnsServers(findStaticDnsServers(networkId))
        .build();
    } catch (Throwable e) {
      log.info("status=failedToFindInterface, nid={}, msg={}", networkId, e.getMessage());
      throw e;
    }
  }

  public static String findDhcpIpAddress(String networkId) {
    try {
      return findNetworkStringValue(networkId, "DhcpIPAddress");
    } catch (Win32Exception e) {
      if (Exceptions.is(e, WinError.ERROR_FILE_NOT_FOUND)) {
        return null;
      }
      throw e;
    }
  }

  public static String findIpAddress(String networkId) {
    try {
      return findNetworkFirstArrValue(networkId, "IPAddress");
    } catch (Win32Exception e) {
      if (Exceptions.is(e, WinError.ERROR_FILE_NOT_FOUND)) {
        return null;
      }
      throw e;
    }
  }

  public static void updateDnsServer(String networkId, List<String> dnsServer) {
    Validate.isTrue(dnsServer.size() <= 2, "You can configure almost to 2 servers, current=%d", dnsServer.size());
    final var key = buildKey(networkId);
    registrySetStringValue(HKEY_LOCAL_MACHINE, key, DNS_SERVER_ATTR, String.join(",", dnsServer));
  }

  private static String findNetworkFirstArrValue(final String networkId, final String property) {
    return findFirstOrNull(registryGetStringArray(HKEY_LOCAL_MACHINE, buildKey(networkId), property));
  }

  private static String findNetworkStringValue(final String networkId, final String property) {
    return registryGetStringValue(HKEY_LOCAL_MACHINE, buildKey(networkId), property);
  }

  private static String findFirstOrNull(final String[] arr) {
    return arr == null ? null : Stream.of(arr)
      .findFirst()
      .orElse(null);
  }

  private static String buildKey(String networkId) {
    return String.format("SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters\\Interfaces\\%s", networkId);
  }

}
