package testing.templates;

import com.mageddo.dnsproxyserver.solver.HostnameQuery;
import com.mageddo.net.IP;

public class HostnameQueryTemplates {

  public static HostnameQuery acmeComWildcard(){
    return HostnameQuery.ofWildcard(HostnameTemplates.ACME_HOSTNAME);
  }

  public static HostnameQuery orangeAcmeComWildcard(){
    return HostnameQuery.ofWildcard(HostnameTemplates.ORANGE_ACME_HOSTNAME);
  }

  public static HostnameQuery nginxWildcard() {
    return HostnameQuery.ofWildcard("nginx-2.dev");
  }

  public static HostnameQuery acmeComLocal() {
    return HostnameQuery.ofWildcard("acme.com.local");
  }

  public static HostnameQuery acmeComQuadA() {
    return HostnameQuery.of(HostnameTemplates.ACME_HOSTNAME, IP.Version.IPV6);
  }

  public static HostnameQuery nginxComBrWildcard() {
    return HostnameQuery.ofWildcard(HostnameTemplates.NGINX_COM_BR);
  }
}
