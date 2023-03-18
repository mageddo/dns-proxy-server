package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.server.dns.solver.HostnameQuery;

public class HostnameQueryTemplates {

  public static HostnameQuery acmeCom(){
    return HostnameQuery.ofWildcard(HostnameTemplates.ACME_HOSTNAME);
  }

  public static HostnameQuery orangeAcmeCom(){
    return HostnameQuery.ofWildcard(HostnameTemplates.ORANGE_ACME_HOSTNAME);
  }

  public static HostnameQuery nginxWildcard() {
    return HostnameQuery.ofWildcard("nginx-2.dev");
  }

  public static HostnameQuery acmeComLocal() {
    return HostnameQuery.ofWildcard("acme.com.local");
  }

  public static HostnameQuery nginxComBr() {
    return HostnameQuery.ofWildcard("nginx.com.br");
  }
}
