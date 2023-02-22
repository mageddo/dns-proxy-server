package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.model.Network;

import static com.mageddo.dnsproxyserver.docker.domain.Network.OTHER;
import static com.mageddo.dnsproxyserver.docker.domain.Network.of;

public class NetworkComparator {

  static int toPriorityOrder(Network n) {
    final var network = of(n.getName());
    if(network == OTHER){
      return of(n.getDriver()).ordinal();
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
