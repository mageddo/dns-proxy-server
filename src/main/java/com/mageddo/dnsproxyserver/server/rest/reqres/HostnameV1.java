package com.mageddo.dnsproxyserver.server.rest.reqres;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.utils.Ips;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

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
      .setIp(Ips.toShortArray(entry.getIp()))
      .setTtl(entry.getTtl())
      .setTarget(entry.getTarget())
      .setType(entry.getType())
      ;
  }

  public Config.Entry toEntry() {
    return Config.Entry.builder()
      .hostname(this.hostname)
      .ttl(this.ttl)
      .ip(this.ip != null ? Ips.toIp(this.ip).toText() : null)
      .type(this.type)
      .target(this.target)
      .id(StringUtils.isBlank(this.id) ? System.nanoTime() : Long.parseLong(this.id))
      .build()
      ;
  }
}
