package com.mageddo.dnsproxyserver.server.rest;

import com.mageddo.dnsproxyserver.docker.DockerNetworkService;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

@Path("/network")
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class NetworkResource {

  private final DockerNetworkService networkService;

  @DELETE
  @Path("/disconnect-containers")
  @Produces(MediaType.APPLICATION_JSON)
  public List<String> delete(@QueryParam("networkId") String id) {
    final var containers = this.networkService.disconnectContainers(id);
    if (containers == null) {
      return Collections.singletonList("Network not found");
    }
    return containers;
  }
}
