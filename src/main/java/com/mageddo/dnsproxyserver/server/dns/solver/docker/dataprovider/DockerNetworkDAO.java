package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;


import com.mageddo.dnsproxyserver.server.dns.solver.docker.Network;

public interface DockerNetworkDAO {
  Network findByNetworkId(String networkId);
}
