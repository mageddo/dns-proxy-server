package com.mageddo.dnsproxyserver.server.rest;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.ConfigDAO;
import com.mageddo.dnsproxyserver.server.rest.reqres.EnvV1;
import lombok.AllArgsConstructor;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/env")
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class EnvResource {

  private final ConfigDAO configDAO;

  @GET
  @Path("/active")
  @Produces(MediaType.APPLICATION_JSON)
  public Object getActive() {
    return EnvV1.of(this.configDAO.findActiveEnv().getName());
  }

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public Object findEnvs() {
    return this.configDAO
      .findEnvs()
      .stream()
      .map(it -> EnvV1.of(it.getName()))
      .toList()
      ;
  }

  @PUT
  @Path("/active")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public void activate(EnvV1 env) {
    this.configDAO.changeActiveEnv(env.getName());
  }


  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public void create(EnvV1 env) {
    this.configDAO.createEnv(Config.Env.empty(env.getName()));
  }

  @DELETE
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public void delete(EnvV1 env) {
    this.configDAO.deleteEnv(env.getName());
  }


}
