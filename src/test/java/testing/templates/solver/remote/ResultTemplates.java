package testing.templates.solver.remote;

import com.mageddo.dnsproxyserver.solver.remote.Result;
import testing.templates.MessageTemplates;
import testing.templates.ResponseTemplates;

public class ResultTemplates {
  public static Result success() {
    return Result
      .builder()
      .successResponse(ResponseTemplates.acmeAResponse())
      .build();
  }

  public static Result error() {
    return Result
      .builder()
      .errorMessage(MessageTemplates.acmeNxDomain())
      .build();
  }
}
