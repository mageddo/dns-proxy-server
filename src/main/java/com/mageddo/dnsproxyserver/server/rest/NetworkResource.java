package com.mageddo.dnsproxyserver.server.rest;

import com.mageddo.commons.lang.Objects;
import com.mageddo.dnsproxyserver.solver.docker.application.DockerNetworkService;
import com.mageddo.http.HttpMapper;
import com.mageddo.http.Request;
import com.mageddo.http.WebServer;
import com.mageddo.http.codec.Encoders;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;
import java.util.Collections;

@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class NetworkResource implements HttpMapper {

  private final DockerNetworkService networkService;

  @Override
  public void map(WebServer server) {
    server.delete("/network/disconnect-containers", exchange -> {
      final var id = Request.queryParam(exchange, "networkId");
      final var containers = this.networkService.disconnectContainers(id);
      Encoders.encodeJson(
          exchange,
          Status.OK,
          Objects.useItOrDefault(containers, () -> Collections.singletonList("Name not found"))
      );
    });
  }
}
