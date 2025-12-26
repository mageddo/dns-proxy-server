package com.mageddo.dnsproxyserver.solver.cache;

import java.time.Duration;

import com.mageddo.dnsproxyserver.solver.NamedResponse;
import com.mageddo.dnsproxyserver.solver.Response;

import org.xbill.DNS.Message;

import lombok.Builder;
import lombok.NonNull;

@lombok.Value
@Builder
public class Value {

  @NonNull
  NamedResponse response;

  boolean hotload;

  public Message getMessage() {
    return this.response.getMessage();
  }

  public Duration getTTL() {
    return this.response.getDpsTtl();
  }

  public String getSolver() {
    return this.response.getSolver();
  }

  public Response getSimpleResponse() {
    return this.response.getResponse();
  }

  public long getTTLAsSeconds() {
    return this.getTTL()
        .toSeconds();
  }

  public int countAnswers() {
    return this.response.getResponse()
        .countAnswers();
  }
}
