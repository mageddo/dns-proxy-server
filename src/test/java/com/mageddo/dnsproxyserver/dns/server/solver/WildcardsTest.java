package com.mageddo.dnsproxyserver.dns.server.solver;

import com.mageddo.dnsproxyserver.server.dns.Hostname;
import org.junit.jupiter.api.Test;

import static com.mageddo.dnsproxyserver.server.dns.Wildcards.buildHostAndWildcards;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WildcardsTest {
  @Test
  void mustGenerateHostsAndWildcardsTo(){
    // arrange
    final var hostname = Hostname.of("bookmarks.mageddo.com");

    // act
    final var result = buildHostAndWildcards(hostname);

    // assert
    assertEquals(
        "[bookmarks.mageddo.com, .bookmarks.mageddo.com, .mageddo.com, .com]",
        result.toString()
    );
  }
}
