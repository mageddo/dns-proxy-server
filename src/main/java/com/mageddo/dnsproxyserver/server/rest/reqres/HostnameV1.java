package com.mageddo.dnsproxyserver.server.rest.reqres;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.net.IP;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class HostnameV1 {

  private String hostname;
  private String ip;
  private String target;
  private int ttl;
  private Config.Entry.Type type;
  private String env;

  public static HostnameV1 of(Config.Entry entry) {
    return new HostnameV1()
        .setHostname(entry.getHostname())
        .setIp(entry.getIpAsText())
        .setTtl(entry.getTtl())
        .setTarget(entry.getTarget())
        .setType(entry.getType())
        ;
  }

  public Config.Entry toEntry() {
    return Config.Entry.builder()
        .hostname(this.hostname)
        .ttl(this.ttl)
        .ip(IP.of(this.ip))
        .type(this.type)
        .target(this.target)
        .build()
        ;
  }
}
