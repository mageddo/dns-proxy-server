package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.model.Network;

public class NetworkComparator {

  static int toPriorityOrder(Network n) {
    return com.mageddo.dnsproxyserver.docker.domain.Network.of(n.getName()).ordinal();
  }

  public static int compare(Network a, Network b) {
    return Integer.compare(toPriorityOrder(a), toPriorityOrder(b));
  }
}
