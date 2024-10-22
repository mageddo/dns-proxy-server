package com.mageddo.dnsproxyserver.solver;

import com.mageddo.net.IpAddr;
import com.mageddo.net.IpAddrs;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class SimpleResolver extends org.xbill.DNS.SimpleResolver implements Resolver {

  public SimpleResolver() throws UnknownHostException {
  }

  public SimpleResolver(String hostname) throws UnknownHostException {
    super(hostname);
  }

  public SimpleResolver(InetSocketAddress addr) {
    super(addr);
  }

  public SimpleResolver(InetAddress host) {
    super(host);
  }

  public SimpleResolver(IpAddr addr) {
    super(IpAddrs.toInetSocketAddress(addr));
  }
}
