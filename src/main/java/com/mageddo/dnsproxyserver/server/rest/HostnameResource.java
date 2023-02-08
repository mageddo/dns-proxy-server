package com.mageddo.dnsproxyserver.server.rest;

import com.mageddo.dnsproxyserver.config.ConfigDAO;
import com.mageddo.dnsproxyserver.server.rest.reqres.HostnameV1;
import lombok.AllArgsConstructor;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/hostname")
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class HostnameResource {

  private final ConfigDAO configDAO;

  @GET
  @Path("/find")
  @Produces(MediaType.APPLICATION_JSON)
  public Object findHostnames(@QueryParam("env") String env, @QueryParam("hostname") String hostname) {
    final var hostnames = this.configDAO.findHostnamesBy(env, hostname);
    if (hostnames == null) {
      return new Object[]{};
    }
    return hostnames
      .stream()
      .map(HostnameV1::of)
      .toList();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public void save(HostnameV1 hostname) {
    this.configDAO.addEntry(hostname.getEnv(), hostname.toEntry());
  }
}
