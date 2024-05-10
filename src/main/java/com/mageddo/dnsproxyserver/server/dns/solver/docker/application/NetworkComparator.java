package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import com.mageddo.dnsproxyserver.server.dns.solver.docker.Network;

import static com.mageddo.dnsproxyserver.server.dns.solver.docker.Network.Name.OTHER;

public class NetworkComparator {

  static int toPriorityOrder(Network n) {
    final var network = Network.Name.of(n.getName());
    if(network == OTHER){
      return Network.Name.of(n.getDriver()).ordinal();
    }
    return network.ordinal();
  }

  public static int compare(String a, String b) {
    return Integer.compare(Network.Name.of(a).ordinal(), Network.Name.of(b).ordinal());
  }

  public static int compare(Network a, Network b) {
    return Integer.compare(toPriorityOrder(a), toPriorityOrder(b));
  }
}
