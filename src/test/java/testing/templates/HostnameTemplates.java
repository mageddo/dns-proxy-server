package testing.templates;

public class HostnameTemplates {
  public static final String ORANGE_ACME_HOSTNAME = "orange.acme.com";
  public static final String ACME_HOSTNAME = "acme.com";
  public static final String COM_WILDCARD = ".com";
  public static final String NGINX_COM_BR = "nginx.com.br";
  public static final String HOST_DOCKER = "host.docker";

  public static String startingWithNameDotDecimalNotation() {
    return "www.192.168.0.1.sslip.io";
  }

  public static String startingWithNameDashSeparationDotDecimalNotation() {
    return "meusite-192.168.0.2.sslip.io";
  }

}
