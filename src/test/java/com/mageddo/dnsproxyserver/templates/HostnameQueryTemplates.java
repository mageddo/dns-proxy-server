package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.server.dns.solver.HostnameQuery;

public class HostnameQueryTemplates {
  public static HostnameQuery acmeCom(){
    return HostnameQuery.ofWildcard(HostnameTemplates.ACME_HOSTNAME);
  }
  public static HostnameQuery orangeAcmeCom(){
    return HostnameQuery.ofWildcard(HostnameTemplates.ORANGE_ACME_HOSTNAME);
  }
}
