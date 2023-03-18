package com.mageddo.dnsproxyserver.server.rest;

import com.mageddo.dnsproxyserver.server.dns.solver.SolversCache;
import com.mageddo.http.HttpMapper;
import com.mageddo.http.WebServer;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import java.util.Map;

import static com.mageddo.http.codec.Encoders.encodeJson;

@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CacheResource implements HttpMapper {

  private final SolversCache cache;

  @Override
  public void map(WebServer server) {

    server.get(
      "/v1/caches/size",
      exchange -> encodeJson(
        exchange,
        Response.Status.OK,
        Map.of("size", this.cache.getSize())
      )
    );

    server.delete(
      "/v1/caches",
      exchange -> {
        this.cache.clear();
        encodeJson(
          exchange,
          Response.Status.OK,
          Map.of("size", this.cache.getSize())
        );
      }
    );

    server.get(
      "/v1/caches",
      exchange -> encodeJson(
        exchange,
        Response.Status.OK,
        this.cache.asMap()
      )
    );
  }

}
