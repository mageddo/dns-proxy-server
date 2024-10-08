package com.mageddo.supporting.circuitbreaker.testing.sandbox.failsafe;

import com.mageddo.supporting.circuitbreaker.testing.sandbox.Result;
import com.mageddo.supporting.circuitbreaker.testing.sandbox.Stats;
import dev.failsafe.CircuitBreakerOpenException;

import java.io.UncheckedIOException;

public class StatsCalculator {
  public static Result calcStats(Stats stats, Runnable r) {
    try {
      r.run();
      stats.success++;
      return Result.SUCCESS;
    } catch (CircuitBreakerOpenException e) {
      stats.openCircuit++;
      return Result.CIRCUIT_OPEN;
    } catch (UncheckedIOException e) {
      stats.error++;
      return Result.ERROR;
    }
  }
}
