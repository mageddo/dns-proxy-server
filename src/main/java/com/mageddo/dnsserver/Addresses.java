package com.mageddo.dnsserver;

import java.util.List;
import java.util.function.Predicate;

import com.mageddo.commons.Collections;
import com.mageddo.net.IP;
import com.mageddo.net.Networks;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Addresses {

  public static List<IP> findBindAddresses(IP address) {
    try {
      return mustFindBindAddresses(address);
    } catch (Exception e) {
      log.warn(
          "status=couldNotFindBestBindAddresses, action=pleaseReportThat, using={}, msg={}",
          address, e.getMessage(), e
      );
      return Collections.singletonList(address);
    }
  }

  private static List<IP> mustFindBindAddresses(IP address) {
    if (!address.isAnyLocal()) {
      return Collections.singletonList(address);
    }
    if (address.versionIs(IP.Version.IPV6)) {
      return Collections.filter(Networks.findMachineIps(), notLinkLocal());
    }
    return Collections.filter(
        Networks.findMachineIps(),
        ip -> ip.versionIs(address.version()),
        notLinkLocal()
    );
  }

  private static Predicate<IP> notLinkLocal() {
    return ip -> !ip.isLinkLocal();
  }
}
