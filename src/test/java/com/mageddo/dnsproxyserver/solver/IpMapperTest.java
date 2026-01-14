package com.mageddo.dnsproxyserver.solver;

import java.util.List;

import com.mageddo.net.IP;

import org.junit.jupiter.api.Test;

import testing.templates.IpTemplates;

import static org.assertj.core.api.Assertions.assertThat;

class IpMapperTest {

  @Test
  void mustReturnAllVersionsAreMixed() {

    final var mixed = List.of(IpTemplates.local(), IpTemplates.localIpv6());

    final var ips = IpMapper.toText(mixed);

    assertThat(ips)
        .hasSize(2)
        .containsExactly(
            "10.10.0.1", "2001:db8:1:0:0:0:0:2"
        );

  }
  @Test
  void musGetOnlyIpv6() {

    final var mixed = List.of(
        IpTemplates.local(),
        IpTemplates.localIpv6(),
        IpTemplates.localIpv6_3()
    );

    final var ips = IpMapper.toText(mixed, IP.Version.IPV6);

    assertThat(ips)
        .hasSize(2)
        .containsExactly(
            "2001:db8:1:0:0:0:0:2",
            "2001:db8:1:0:0:0:0:3"
        );

  }
}
