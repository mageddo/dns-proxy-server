package com.mageddo.dnsproxyserver.solver.remote.application.mapper;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.remote.Request;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.mageddo.dns.utils.Messages.simplePrint;

@Slf4j
public class ResultMapper {

  static final String QUERY_TIMED_OUT_MSG = "Query timed out";

  public static Result from(CompletableFuture<Message> resFuture, Request request){
    return transformToResult(resFuture, request);
  }

  private static Result transformToResult(CompletableFuture<Message> resFuture, Request request) {
    final var res = findFutureRes(resFuture, request);
    if (res == null) {
      return Result.empty();
    }

    if (Messages.isSuccess(res)) {
      log.trace(
        "status=found, i={}, time={}, req={}, res={}, server={}",
        request.getResolverIndex(), request.getTime(), simplePrint(request.getQuery()),
        simplePrint(res), request.getResolverAddress()
      );
      return Result.fromSuccessResponse(Response.success(res));
    } else {
      log.trace(
        "status=notFound, i={}, time={}, req={}, res={}, server={}",
        request.getResolverIndex(), request.getTime(), simplePrint(request.getQuery()),
        simplePrint(res), request.getResolverAddress()
      );
      return Result.fromErrorMessage(res);
    }
  }

  private static Message findFutureRes(CompletableFuture<Message> resFuture, Request request) {
    try {
      return Messages.setFlag(resFuture.get(), Flags.RA);
    } catch (InterruptedException | ExecutionException e) {
      checkCircuitError(e, request);
      return null;
    }
  }

  private static void checkCircuitError(Exception e, Request request) {
    if (e.getCause() instanceof IOException) {
      final var time = request.getElapsedTimeInMs();
      if (e.getMessage().contains(QUERY_TIMED_OUT_MSG)) {
        log.info(
          "status=timedOut, i={}, time={}, req={}, msg={} class={}",
          request.getResolverIndex(), time, simplePrint(request.getQuery()), e.getMessage(), ClassUtils.getSimpleName(e)
        );
        throw new CircuitCheckException(buildErrorMsg(e, request), e);
      }
      log.warn(
        "status=failed, i={}, time={}, req={}, server={}, errClass={}, msg={}",
        request.getResolverIndex(), time, simplePrint(request.getQuery()), request.getResolverAddress(),
        ClassUtils.getSimpleName(e), e.getMessage(), e
      );
    } else {
      throw new RuntimeException(buildErrorMsg(e, request), e);
    }
  }

  private static String buildErrorMsg(Exception e, Request request) {
    return String.format(
      "( req=%s, server=%s, msg=%s )",
      simplePrint(request.getQuery()), request.getResolverAddress(), e.getMessage()
    );
  }

}
