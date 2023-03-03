package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsproxyserver.server.dns.Hostname;
import com.mageddo.dnsproxyserver.templates.EnvTemplates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.mageddo.dnsproxyserver.templates.EnvTemplates.MAGEDDO_COM_CAMEL_CASE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ConfigDAOJsonTest {

  @Spy
  @InjectMocks
  ConfigDAOJson dao;

  @Test
  void mustDoCaseInsensitiveFind() {
    // arrange
    final var hostname = Hostname.of(EnvTemplates.MAGEDDO_COM);
    final var env = EnvTemplates.buildWithCamelCaseHost();

    doReturn(env)
      .when(this.dao)
      .findActiveEnv();

    // act
    final var entry = this.dao.findEntryForActiveEnv(hostname);

    // assert
    assertNotNull(entry);
    assertEquals(MAGEDDO_COM_CAMEL_CASE, entry.getHostname());
  }
}
