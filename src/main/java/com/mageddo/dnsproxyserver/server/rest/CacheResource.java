package com.mageddo.dnsproxyserver.server.rest;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response;

import com.mageddo.dnsproxyserver.server.rest.reqres.CacheEntryResV1;
import com.mageddo.dnsproxyserver.solver.cache.CacheName.Name;
import com.mageddo.dnsproxyserver.solver.cache.SolverCacheFactory;
import com.mageddo.http.HttpMapper;
import com.mageddo.http.Request;
import com.mageddo.http.WebServer;
import com.sun.net.httpserver.HttpExchange;

import lombok.RequiredArgsConstructor;

import static com.mageddo.http.codec.Encoders.encodeJson;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
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
            CacheEntryResV1.of(this.factory.findCachesAsMap(buildCacheName(exchange)))
        )
    );
  }

  private static Name buildCacheName(HttpExchange exchange) {
    return Name.fromName(Request.queryParam(exchange, CACHE_NAME_PARAM));
  }

}
