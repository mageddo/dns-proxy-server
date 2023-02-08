package com.mageddo.dnsproxyserver.server.rest.reqres;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.server.dns.IP;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class HostnameV1 {

  private String id;
  private String hostname;
  private Short[] ip;
  private String target;
  private int ttl;
  private Config.Entry.Type type;
  private String env;

  public static HostnameV1 of(Config.Entry entry) {
    return new HostnameV1()
      .setHostname(entry.getHostname())
      .setId(String.valueOf(entry.getId()))
      .setIp(IP.of(entry.getIp()).toShortArray())
      .setTtl(entry.getTtl())
      .setTarget(entry.getTarget())
      .setType(entry.getType())
      ;
  }
}
