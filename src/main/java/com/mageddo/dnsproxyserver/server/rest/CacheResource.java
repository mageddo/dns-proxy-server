package com.mageddo.dnsproxyserver.server.rest;

import com.mageddo.dnsproxyserver.server.dns.solver.SolverCacheFactory;
import com.mageddo.dnsproxyserver.server.dns.solver.CacheName.Name;
import com.mageddo.http.HttpMapper;
import com.mageddo.http.Request;
import com.mageddo.http.WebServer;
import com.sun.net.httpserver.HttpExchange;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response;

import static com.mageddo.http.codec.Encoders.encodeJson;

@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CacheResource implements HttpMapper {

  public static final String CACHE_NAME_PARAM = "name";
  private final SolverCacheFactory factory;

  @Override
  public void map(WebServer server) {

    server.get(
      "/v1/caches/size",
      exchange -> {
        encodeJson(
          exchange,
          Response.Status.OK,
          this.factory.findInstancesSizeMap(buildCacheName(exchange))
        );
      }
    );

    server.delete(
      "/v1/caches",
      exchange -> {
        this.factory.clear(buildCacheName(exchange));
        encodeJson(
          exchange,
          Response.Status.OK,
          this.factory.findInstancesSizeMap(buildCacheName(exchange))
        );
      }
    );

    server.get(
      "/v1/caches",
      exchange -> encodeJson(
        exchange,
        Response.Status.OK,
        this.factory.findCachesAsMap(buildCacheName(exchange))
      )
    );
  }

  private static Name buildCacheName(HttpExchange exchange) {
    return Name.fromName(Request.queryParam(exchange, CACHE_NAME_PARAM));
  }

}
