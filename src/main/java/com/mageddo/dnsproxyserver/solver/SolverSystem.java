package com.mageddo.dnsproxyserver.solver;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.Config.Entry.Type;
import com.mageddo.dnsproxyserver.config.ConfigEntryTypes;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.usecase.HostMachineService;
import com.mageddo.net.IP;

import org.xbill.DNS.Message;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.mageddo.dns.utils.Messages.findQuestionTypeCode;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class SolverSystem implements Solver {

  private final HostMachineService machineService;

  @Override
  public Response handle(Message query) {
    final var hostname = Messages.findQuestionHostname(query);

    final var questionType = Messages.findQuestionType(query);
    if (isNotSupported(questionType)) {
      log.debug(
          "status=unsupportedType, type={}, query={}",
          findQuestionTypeCode(query), Messages.simplePrint(query)
      );
      return null;
    }

    final var config = Configs.getInstance();
    // fixme fazer case com hostname + search domain
    if (hostname.isEqualTo(config.getHostMachineHostname())) {

      if (questionType.isHttps()) {
        return Response.internalSuccess(Messages.notSupportedHttps(query));
      }

      final var ip = this.findHostMachineIP(questionType.toVersion());
      log.debug("status=solvingHostMachineName, host={}, ip={}", hostname, ip);
      return ResponseMapper.toDefaultSuccessAnswer(query, ip, questionType.toVersion());
    }
    return null;
  }

  private static boolean isNotSupported(Type questionType) {
    return ConfigEntryTypes.isNot(questionType, Type.A, Type.AAAA, Type.HTTPS);
  }

  IP findHostMachineIP(IP.Version version) {
    return this.machineService.findHostMachineIP(version);
  }

}
