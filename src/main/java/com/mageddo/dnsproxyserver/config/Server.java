package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsserver.SimpleServer;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Server {

  private Integer webServerPort;

  private Integer dnsServerPort;
  private Integer dnsServerNoEntriesResponseCode;

  private SimpleServer.Protocol serverProtocol;


}
