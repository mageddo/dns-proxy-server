package com.mageddo.dnsproxyserver.solver;

import java.time.Duration;
import java.time.LocalDateTime;

import org.xbill.DNS.Message;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class NamedResponse {

  @NonNull
  String solver;

  Response response;

  public static NamedResponse of(Response response, String solverName) {
    return NamedResponse.builder()
        .solver(solverName)
        .response(response)
        .build();
  }

  public LocalDateTime getCreatedAt() {
    return this.response.getCreatedAt();
  }

  public NamedResponse withMessage(Message message) {
    return of(this.response.withMessage(message), this.solver);
  }

  public Message getMessage() {
    return this.response.getMessage();
  }

  public Duration getDpsTtl() {
    return this.response.getDpsTtl();
  }

  public NamedResponse withTTL(Duration ttl) {
    return of(this.response.withTTL(ttl), this.solver);
  }

  public boolean hasResponse() {
    return this.response == null;
  }
}
