package com.mageddo.dnsproxyserver.server.rest;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.ConfigDAO;
import com.mageddo.dnsproxyserver.server.rest.reqres.EnvV1;
import com.mageddo.http.HttpMapper;
import com.mageddo.http.WebServer;
import com.mageddo.http.codec.Decoders;
import com.mageddo.http.codec.Encoders;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;

@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class EnvResource implements HttpMapper {

  private final ConfigDAO configDAO;

  @Override
  public void map(WebServer server) {

    server.get("/env/active", exchange -> {
      Encoders.encodeJson(
          exchange,
          Status.OK,
          EnvV1.of(this.configDAO.findActiveEnv().getName())
      );
    });

    server.get("/env", exchange -> {
      final var result = this.configDAO
          .findEnvs()
          .stream()
          .map(it -> EnvV1.of(it.getName()))
          .toList();
      Encoders.encodeJson(exchange, Status.OK, result);
    });

    server.post("/env", exchange -> {
      final var env = Decoders.jsonDecode(exchange, EnvV1.class);
      this.configDAO.createEnv(Config.Env.empty(env.getName()));
    });

    server.put("/env/active", exchange -> {
      final var env = Decoders.jsonDecode(exchange, EnvV1.class);
      this.configDAO.changeActiveEnv(env.getName());
    });

    server.delete("/env", exchange -> {
      final var env = Decoders.jsonDecode(exchange, EnvV1.class);
      this.configDAO.deleteEnv(env.getName());
    });

  }
}
