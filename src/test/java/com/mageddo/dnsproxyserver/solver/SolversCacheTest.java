package com.mageddo.dnsproxyserver.solver;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.solver.CacheName.Name;
import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.SolverCache;
import testing.templates.MessageTemplates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xbill.DNS.Flags;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SolversCacheTest {

  SolverCache cache = new SolverCache(Name.GLOBAL);

  @Test
  void mustCacheAndGetValidResponse(){

    // arrange
    final var req = MessageTemplates.acmeAQuery();

    // act
    final var res = this.cache.handle(req, message -> Response.internalSuccess(Messages.aAnswer(message, "0.0.0.0")));

    // assert
    assertNotNull(res);
    assertEquals(1, this.cache.getSize());

    final var header = res.getHeader();
    assertEquals(req.getHeader().getID(), res.getHeader().getID());
    assertTrue(header.getFlag(Flags.QR));

  }

  @Test
  void cantCacheWhenDelegateSolverHasNoAnswer(){
    // arrange
    final var query = MessageTemplates.acmeAQuery();

    // act
    final var res = this.cache.handle(query, message -> null);

    // assert
    assertNull(res);
    assertEquals(0, this.cache.getSize());
  }

}
