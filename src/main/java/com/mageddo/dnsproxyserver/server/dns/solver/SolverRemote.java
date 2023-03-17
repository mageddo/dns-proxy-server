package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.server.dns.Messages;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.xbill.DNS.Message;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Resolver;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

import static com.mageddo.dnsproxyserver.server.dns.Messages.simplePrint;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class SolverRemote implements Solver {

  private final RemoteResolvers delegate;

  @Override
  public Response handle(Message query) {
    Message lastErrorMsg = null;
    for (int i = 0; i < this.delegate.resolvers().size(); i++) {
      final Resolver resolver = this.delegate.resolvers().get(i);
      try {
        final var res = resolver.send(query);
        if (res.getRcode() == Rcode.NOERROR) {
          log.trace("status=found, i={}, req={}, res={}, server={}", i, simplePrint(query), simplePrint(res), resolver);
          return Response.of(res, Messages.findTTL(res)); // fixme calculate the best ttl here
        } else {
          lastErrorMsg = res;
          log.trace("status=notFound, i={}, req={}, res={}, server={}", i, simplePrint(query), simplePrint(res), resolver);
        }
      } catch (IOException e) {
        if (e.getMessage().contains("Timed out while trying")) {
          log.info(
            "status=timedOut, req={}, msg={} class={}",
            simplePrint(query), e.getMessage(), ClassUtils.getSimpleName(e)
          );
          continue;
        }
        log.warn(
          "status=failed, i={}, req={}, server={}, errClass={}, msg={}",
          i, simplePrint(query), resolver, ClassUtils.getSimpleName(e), e.getMessage(), e
        );
      }
    }
    if (lastErrorMsg == null) {
      return null;
    }
    return Response.of(lastErrorMsg, Messages.findTTL(lastErrorMsg)); // fixme calculate the best ttl here
  }
}
