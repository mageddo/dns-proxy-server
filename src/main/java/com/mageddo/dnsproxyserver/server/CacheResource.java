package com.mageddo.dnsproxyserver.server;

import com.mageddo.dnsproxyserver.server.dns.solver.SolversCache;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/v1/caches")
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CacheResource {

  private final SolversCache cache;

  @GET
  @Path("/size")
  @Produces(MediaType.APPLICATION_JSON)
  public Object findCaches() {
    return Map.of("size", this.cache.getSize());
  }
}
