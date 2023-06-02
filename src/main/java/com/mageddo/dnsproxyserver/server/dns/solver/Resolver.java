package com.mageddo.dnsproxyserver.server.dns.solver;


import java.net.InetSocketAddress;

public interface Resolver extends org.xbill.DNS.Resolver {
  InetSocketAddress getAddress();
}
