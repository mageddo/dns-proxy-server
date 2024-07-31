package testing.templates.solver.remote;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dnsproxyserver.solver.remote.Result;

import java.util.function.Supplier;

public class ResultSupplierTemplates {
  public static Supplier<Result> alwaysFail() {
    return () -> {
      throw new CircuitCheckException("Mocked Exception");
    };
  }

  public static Supplier<Result> alwaysSuccess() {
    return () -> {
      return null;
    };
  }
}
