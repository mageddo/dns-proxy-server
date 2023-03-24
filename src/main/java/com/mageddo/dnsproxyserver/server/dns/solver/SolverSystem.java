package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.config.Config.Entry.Type;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.dnsproxyserver.usecase.HostMachineService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class SolverSystem implements Solver {

  private final HostMachineService machineService;

  @Override
  public Response handle(Message query) {
    final var hostname = Messages.findQuestionHostname(query);
    final var questionType = Messages.findQuestionType(query);
    if (questionType.isNot(Type.A, Type.AAAA)) {
      log.debug("status=unsupportedType, type={}, query={}", questionType, Messages.simplePrint(query));
      return null;
    }
    final var config = Configs.getInstance();
    if (hostname.isEqualTo(config.getHostMachineHostname())) { // fixme fazer case com hostname + search domain
      final var ip = this.machineService.findHostMachineIP();
      if (ip == null) {
        log.debug("status=hostMachineIpNotFound, host={}", hostname);
        return null;
      }
      log.debug("status=solvingHostMachineName, host={}, ip={}", hostname, ip);
      return Response.of(Messages.aAnswer(query, ip.toText()), Messages.DEFAULT_TTL_DURATION);
    }
    return null;
  }

}
