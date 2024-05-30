package com.mageddo.dnsproxyserver.solver;

import org.apache.commons.lang3.ClassUtils;
import org.xbill.DNS.Message;

public interface Solver {

  Response handle(Message query);

  default String name() {
    return ClassUtils.getSimpleName(getClass());
  }

  default boolean is(String name){
    return this.name().equals(name);
  }
}
