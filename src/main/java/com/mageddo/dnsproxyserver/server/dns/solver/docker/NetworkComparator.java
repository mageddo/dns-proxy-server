package com.mageddo.dnsproxyserver.server.dns.solver.docker;

import static com.mageddo.dnsproxyserver.server.dns.solver.docker.Network.Priority.OTHER;

public class NetworkComparator {

  static int toPriorityOrder(Network n) {
    final var network = Network.Priority.of(n.getName());
    if(network == OTHER){
      return Network.Priority.of(n.getDriver()).ordinal();
    }
    return network.ordinal();
  }

  public static int compare(String a, String b) {
    return Integer.compare(of(a).ordinal(), of(b).ordinal());
  }

  public static int compare(Network a, Network b) {
    return Integer.compare(toPriorityOrder(a), toPriorityOrder(b));
  }
}
