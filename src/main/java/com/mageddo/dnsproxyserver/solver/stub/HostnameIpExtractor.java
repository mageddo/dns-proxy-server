package com.mageddo.dnsproxyserver.solver.stub;

import com.mageddo.dnsproxyserver.solver.stub.addressexpression.AddressExpressions;
import com.mageddo.dnsproxyserver.solver.stub.addressexpression.ParseException;
import com.mageddo.net.IP;
import org.apache.commons.lang3.Validate;

public class HostnameIpExtractor {

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
