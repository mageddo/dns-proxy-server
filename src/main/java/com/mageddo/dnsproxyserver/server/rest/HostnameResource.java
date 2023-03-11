package com.mageddo.dnsproxyserver.server.rest;

import com.mageddo.dnsproxyserver.config.ConfigDAO;
import com.mageddo.dnsproxyserver.server.rest.reqres.HostnameV1;
import com.mageddo.dnsproxyserver.server.rest.reqres.Message;
import com.mageddo.http.HttpMapper;
import com.mageddo.http.Request;
import com.mageddo.http.WebServer;
import com.mageddo.http.codec.Decoders;
import com.mageddo.http.codec.Encoders;
import lombok.AllArgsConstructor;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response.Status;

@Path("/hostname")
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class HostnameResource implements HttpMapper {

  private final ConfigDAO configDAO;

  @Override
  public void handle(WebServer server) {

    server.get("/hostname/find", exchange -> {
      final var env = Request.queryParam(exchange, "env");
      final var hostname = Request.queryParam(exchange, "hostname");
      final var hostnames = this.configDAO.findHostnamesBy(env, hostname);
      if (hostnames == null) {
        Encoders.encodeJson(exchange, Status.OK, new Object[]{});
      } else {
        final var result = hostnames
          .stream()
          .map(HostnameV1::of)
          .toList();
        Encoders.encodeJson(exchange, Status.OK, result);
      }
    });

    server.post("/hostname", exchange -> {
      final var hostname = Decoders.jsonDecode(exchange, HostnameV1.class);
      this.configDAO.addEntry(hostname.getEnv(), hostname.toEntry());
    });

    server.put("/hostname", exchange -> {
      final var hostname = Decoders.jsonDecode(exchange, HostnameV1.class);
      this.configDAO.updateEntry(hostname.getEnv(), hostname.toEntry());
    });

    server.delete("/hostname", exchange -> {
      final var hostname = Decoders.jsonDecode(exchange, HostnameV1.class);
      final var removed = this.configDAO.removeEntry(hostname.getEnv(), hostname.getHostname());
      if (removed) {
        Encoders.noBody(exchange, Status.OK);
      } else {
        final var msg = Message.of(
          Status.BAD_REQUEST.getStatusCode(),
          String.format("Can't delete hostname: %s", hostname.getHostname())
        );
        Encoders.encodeJson(exchange, Status.BAD_REQUEST, msg);
      }
    });

  }
}
