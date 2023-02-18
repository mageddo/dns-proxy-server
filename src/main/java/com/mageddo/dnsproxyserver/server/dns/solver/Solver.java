package com.mageddo.dnsproxyserver.server.dns.solver;

import org.apache.commons.lang3.ClassUtils;
import org.xbill.DNS.Message;

public interface Solver {
  Message handle(Message reqMsg);

  default String name() {
    return ClassUtils.getSimpleName(getClass());
  }
}
