package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.server.dns.Messages;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;
import org.xbill.DNS.Resolver;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.UncheckedIOException;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class RemoteSolver implements Solver {

  private final Resolver delegate;

  @Override
  public Message handle(Message req) {
    try {
      final var res = this.delegate.send(req);
      log.info("status=handled, req={}, res={}", Messages.simplePrint(req), Messages.simplePrint(res));
      return res;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
