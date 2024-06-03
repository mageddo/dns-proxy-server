package com.mageddo.dnsproxyserver.solver.remote;

import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.SolverRemote;
import lombok.Builder;
import lombok.Value;
import org.xbill.DNS.Message;

import java.util.Optional;

@Value
@Builder
public class Result {

  private Response successResponse;
  private Message errorMessage;

  public static Result empty() {
    return Result.builder().build();
  }

  public static Result fromErrorMessage(Message message) {
    return builder().errorMessage(message).build();
  }

  public static Result fromSuccessResponse(Response res) {
    return Result.builder().successResponse(res).build();
  }

  public boolean hasSuccessMessage() {
    return this.successResponse != null;
  }

  public boolean hasErrorMessage() {
    return this.errorMessage != null;
  }

  public Response getErrorResponse() {
    return Optional.ofNullable(this.errorMessage)
      .map(Response::nxDomain)
      .orElse(null);
  }
}
