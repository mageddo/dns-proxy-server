package com.mageddo.dnsproxyserver.solver.stub;

import com.mageddo.dns.Hostname;
import com.mageddo.dnsproxyserver.solver.stub.addressexpression.AddressExpressions;
import com.mageddo.dnsproxyserver.solver.stub.addressexpression.ParseException;
import com.mageddo.net.IP;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

@Slf4j
public class HostnameIpExtractor {

  public static IP safeExtract(Hostname hostname, String domain) {
    try {
      return extract(hostname, domain);
    } catch (Exception e) {
      log.info("status=failedToExtractIpFromHostname, hostname={}, msg={}", hostname, e.getMessage(), e);
      return null;
    }
  }

  public static IP extract(Hostname hostname, String domain) {
    return extract(hostname.getCanonicalValue(), domain);
  }

  public static IP extract(String hostname, String domain) {

    hostname = removeDomainFrom(hostname, domain);
    Validate.notBlank(hostname, "Hostname is empty");

    RuntimeException lastException = null;
    for (int i = 0; i < hostname.length(); i++) {
      try {
        return AddressExpressions.toIp(hostname.substring(i));
      } catch (ParseException e) {
        lastException = e;
      }
    }

    throw lastException;
  }

  static String removeDomainFrom(String hostname, String domain) {
    final var idx = hostname.indexOf(domain);
    if (idx < 0) {
      return hostname;
    }
    return hostname.substring(0, idx - 1);
  }

}
