package com.mageddo.dnsproxyserver.solver;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class SimpleResolver extends org.xbill.DNS.SimpleResolver implements Resolver {

  public SimpleResolver() throws UnknownHostException {
  }

  public SimpleResolver(String hostname) throws UnknownHostException {
    super(hostname);
  }

  public SimpleResolver(InetSocketAddress host) {
    super(host);
  }

  public SimpleResolver(InetAddress host) {
    super(host);
  }
}
