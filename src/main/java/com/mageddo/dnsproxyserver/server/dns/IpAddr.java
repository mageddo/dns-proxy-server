package com.mageddo.dnsproxyserver.server.dns;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mageddo.commons.lang.regex.Regexes;
import com.mageddo.dnsproxyserver.json.converter.IPConverter;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.regex.Pattern;

@Value
@Builder
public class IpAddr {

  public static final Pattern IP_ADDR_REGEX =
    Pattern.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})((?::(\\d+)|))$");

  @NonNull
  @JsonDeserialize(using = IPConverter.Deserializer.class)
  @JsonSerialize(using = IPConverter.Serializer.class)
  private IP ip;

  private Integer port;

  public int getPortOrDef(int def) {
    return this.port == null ? def : this.port;
  }

  /***
   *
   * @param addr something like 192.168.0.1 or 192.168.0.1:4411
   * @return parsed object.
   */
  public static IpAddr of(String addr) {
    Validate.isTrue(
      Regexes.matcher(StringUtils.trimToEmpty(addr), IP_ADDR_REGEX).matches(),
      "Need to pass a valid addr: actual=%s", addr
    );
    final var groups = Regexes.groups(addr, IP_ADDR_REGEX);
    return IpAddr
      .builder()
      .ip(IP.of(groups.get(1)))
      .port(groups.get(3, s -> StringUtils.isBlank(s) ? null : Integer.parseInt(s)))
      .build();
  }

  public String getRawIP() {
    return this.ip.raw();
  }
}
