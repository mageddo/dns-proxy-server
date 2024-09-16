package testing.templates.solver.remote;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dnsproxyserver.solver.remote.Result;

import java.util.concurrent.atomic.AtomicInteger;
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

  public static WithCallsCounter withCallsCounterNullRes() {
    return withCallsCounter(() -> null);
  }

  public static WithCallsCounter withCallsCounter(Supplier<Result> sup) {
    return new WithCallsCounter(sup);
  }

  public static class WithCallsCounter implements Supplier<Result> {

    final AtomicInteger calls = new AtomicInteger();
    Supplier<Result> delegate;

    public WithCallsCounter(Supplier<Result> delegate) {
      this.delegate = delegate;
    }

    @Override
    public Result get() {
      this.calls.incrementAndGet();
      return this.delegate.get();
    }

    public int getCalls() {
      return this.calls.get();
    }
  }
}
