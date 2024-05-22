package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsproxyserver.config.Config.Entry.Type;
import com.mageddo.dnsproxyserver.config.dataprovider.PersistentConfigDAOJson;
import com.mageddo.dnsproxyserver.server.dns.solver.HostnameQuery;
import testing.templates.EnvTemplates;
import testing.templates.HostnameQueryTemplates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static testing.templates.EnvTemplates.MAGEDDO_COM_CAMEL_CASE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PersistentConfigDAOJsonTest {

  @Spy
  @InjectMocks
  PersistentConfigDAOJson dao;

  @Test
  void mustDoCaseInsensitiveFind() {
    // arrange
    final var hostname = HostnameQuery.of(EnvTemplates.MAGEDDO_COM);
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

  @Test
  void mustSolveQuadARecord(){
    // arrange
    final var query = HostnameQueryTemplates.acmeComQuadA();

    final var env = EnvTemplates.acmeQuadA();
    doReturn(env)
      .when(this.dao)
      .findActiveEnv();

    // act
    final var found = this.dao.findEntryForActiveEnv(query);

    // assert
    assertNotNull(found);
  }

  @Test
  void mustSolveQueriedTypeWhenTwoDifferentTypesAreAvailable(){
    // arrange
    final var query = HostnameQueryTemplates.acmeComQuadA();

    final var env = EnvTemplates.acmeAAndQuadA();
    doReturn(env)
      .when(this.dao)
      .findActiveEnv();

    // act
    final var found = this.dao.findEntryForActiveEnv(query);

    // assert
    assertNotNull(found);
    assertEquals(Type.AAAA, found.getType());
  }


  @Test
  void mustReturnWhatExistsWhenNotBestMatchIsFound(){
    // arrange
    final var query = HostnameQueryTemplates.acmeComQuadA();

    final var env = EnvTemplates.acmeCname();
    doReturn(env)
      .when(this.dao)
      .findActiveEnv();

    // act
    final var found = this.dao.findEntryForActiveEnv(query);

    // assert
    assertNotNull(found);
    assertEquals(Type.CNAME, found.getType());
  }

}
