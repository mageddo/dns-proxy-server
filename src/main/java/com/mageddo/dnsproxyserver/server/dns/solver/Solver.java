package com.mageddo.dnsproxyserver.server.dns.solver;

import org.xbill.DNS.Message;

public interface Solver {

  Message handle(Message reqMsg);

  default byte priority(){
    return Byte.MAX_VALUE;
  }
}
