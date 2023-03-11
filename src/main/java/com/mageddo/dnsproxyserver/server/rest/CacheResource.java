package com.mageddo.dnsproxyserver.server.rest;

import com.mageddo.dnsproxyserver.server.dns.solver.SolversCache;
import com.mageddo.http.HttpMapper;
import com.mageddo.http.WebServer;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

import static com.mageddo.http.codec.Encoders.encodeJson;

@Path("/v1/caches")
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CacheResource implements HttpMapper {

  private final SolversCache cache;

  @GET
  @Path("/size")
  @Produces(MediaType.APPLICATION_JSON)
  public Object findCaches() {
    return Map.of("size", this.cache.getSize());
  }

  @Override
  public void map(WebServer server) {
    server.map(
      "/v1/caches/size",
      exchange -> encodeJson(
        exchange,
        Response.Status.OK,
        Map.of("size", this.cache.getSize())
      )
    );
  }

}
