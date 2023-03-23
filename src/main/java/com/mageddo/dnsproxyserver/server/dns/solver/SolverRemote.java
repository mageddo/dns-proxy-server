package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.server.dns.Messages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Resolver;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.time.Duration;

import static com.mageddo.dnsproxyserver.server.dns.Messages.simplePrint;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SolverRemote implements Solver {

  public static final Duration DEFAULT_SUCCESS_TTL = Duration.ofMinutes(5);
  public static final Duration DEFAULT_NXDOMAIN_TTL = Duration.ofMinutes(60);

  private final RemoteResolvers delegate;

  @Override
  public Response handle(Message query) {
    final var stopWatch = StopWatch.createStarted();
    Message lastErrorMsg = null;
    for (int i = 0; i < this.delegate.resolvers().size(); i++) {
      stopWatch.split();
      final Resolver resolver = this.delegate.resolvers().get(i);
      try {

        final var res = Messages.setFlag(resolver.send(query), Flags.RA);

        if (res.getRcode() == Rcode.NOERROR) {
          log.trace(
            "status=found, i={}, time={}, req={}, res={}, server={}",
            i, stopWatch.getTime(), simplePrint(query), simplePrint(res), resolver
          );
          return Response.of(res, DEFAULT_SUCCESS_TTL);
        } else {
          lastErrorMsg = res;
          log.trace(
            "status=notFound, i={}, time={}, req={}, res={}, server={}",
            i, stopWatch.getTime(), simplePrint(query), simplePrint(res), resolver
          );
        }
      } catch (IOException e) {
        final var time = stopWatch.getTime() - stopWatch.getSplitTime();
        if (e.getMessage().contains("Timed out while trying")) {
          log.info(
            "status=timedOut, i={}, time={}, req={}, msg={} class={}",
            i, time, simplePrint(query), e.getMessage(), ClassUtils.getSimpleName(e)
          );
          continue;
        }
        log.warn(
          "status=failed, i={}, time={}, req={}, server={}, errClass={}, msg={}",
          i, time, simplePrint(query), resolver, ClassUtils.getSimpleName(e), e.getMessage(), e
        );
      }
    }
    final var hasErrorResult = lastErrorMsg == null;
    log.debug("status=finally, time={}, hasErrorResult={}", stopWatch.getTime(), hasErrorResult);
    if (hasErrorResult) {
      return null;
    }
    return Response.of(lastErrorMsg, DEFAULT_NXDOMAIN_TTL);
  }
}
