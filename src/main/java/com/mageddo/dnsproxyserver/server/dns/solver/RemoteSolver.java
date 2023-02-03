package com.mageddo.dnsproxyserver.server.dns.solver;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Resolver;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.UncheckedIOException;

import static com.mageddo.dnsproxyserver.server.dns.Messages.simplePrint;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class RemoteSolver implements Solver {

  private final RemoteResolvers delegate;

  @Override
  public Message handle(Message req) {
    UncheckedIOException lastError = null;
    for (int i = 0; i < this.delegate.resolvers().size(); i++) {
      final Resolver resolver = this.delegate.resolvers().get(i);
      try {
        final var res = resolver.send(req);
        if (res.getRcode() == Rcode.NOERROR) {
          log.debug("status=found, i={}, req={}, res={}, server={}", i, simplePrint(req), simplePrint(res), resolver);
          return res;
        } else {
          log.debug("status=notFound, i={}, req={}, res={}, server={}", i, simplePrint(req), simplePrint(res), resolver);
        }
      } catch (IOException e) {
        log.warn("status=failed, i={}, req={}, server={}", i, simplePrint(req), resolver);
        lastError = new UncheckedIOException(e);
      }
    }
    if (lastError != null) {
      throw lastError;
    }
    return null;
  }
}
