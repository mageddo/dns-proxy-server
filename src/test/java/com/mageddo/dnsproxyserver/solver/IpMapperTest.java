package com.mageddo.dnsproxyserver.solver;

import java.util.List;

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
            "a", "b"
        );

  }
}
