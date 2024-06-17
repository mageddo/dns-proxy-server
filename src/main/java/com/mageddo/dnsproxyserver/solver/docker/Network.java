package com.mageddo.dnsproxyserver.solver.docker;

import com.mageddo.net.IP;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Value
@Builder
public class Network {

  @NonNull
  private String name;

  @NonNull
  private String driver;

  @Builder.Default
  @NonNull
  private List<IP> gateways = Collections.emptyList();

  private boolean ipv6Active;

  public IP getGateway(IP.Version version) {
    return this.gateways.stream()
      .filter(it -> Objects.equals(it.version(), version))
      .findFirst()
      .orElse(null);
  }

  public boolean hasAnyGateway() {
    return !this.gateways.isEmpty();
  }

  public enum Name {

    DPS,
    BRIDGE,
    HOST,
    OTHER;

    public static Name of(String name) {
      return EnumUtils.getEnumIgnoreCase(Name.class, name, OTHER);
    }

    public String lowerCaseName() {
      return StringUtils.lowerCase(this.name());
    }

    public boolean equalTo(String networkMode) {
      return Objects.equals(of(networkMode), this);
    }
  }
}
