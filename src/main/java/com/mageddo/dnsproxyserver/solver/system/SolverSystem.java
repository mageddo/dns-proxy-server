package com.mageddo.dnsproxyserver.solver.system;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.solver.AddressResolution;
import com.mageddo.dnsproxyserver.solver.QueryResponseHandler;
import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.Solver;
import com.mageddo.dnsproxyserver.solver.SupportedTypes;
import com.mageddo.net.IP;

import org.xbill.DNS.Message;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class SolverSystem implements Solver {

  public static final String NAME = "SolverSystem";
  private final HostMachineService machineService;

  private final QueryResponseHandler handler = QueryResponseHandler.builder()
      .solverName(this.name())
      .supportedTypes(SupportedTypes.ADDRESSES)
      .build();

  @Override
  public Response handle(Message query) {

    return this.handler.mapExactFromResolution(query, hostnameQuery -> {
          final var hostname = hostnameQuery.getHostname();
          final var config = Configs.getInstance();
          if (hostname.isEqualTo(config.getHostMachineHostname())) {
            final var ip = this.findHostMachineIP(hostnameQuery.getVersion());
            return AddressResolution.matched(ip);
          }
          return AddressResolution.notMatched();
        }
    );

  }

  IP findHostMachineIP(IP.Version version) {
    return this.machineService.findHostMachineIP(version);
  }

  @Override
  public String name() {
    return NAME;
  }
}
