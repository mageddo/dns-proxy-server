package com.mageddo.dnsproxyserver.solver.remote;


import java.net.InetSocketAddress;

public interface Resolver extends org.xbill.DNS.Resolver {
  InetSocketAddress getAddress();
}
