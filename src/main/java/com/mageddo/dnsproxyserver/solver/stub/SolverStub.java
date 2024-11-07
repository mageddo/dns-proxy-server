package com.mageddo.dnsproxyserver.solver.stub;

import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.Solver;
import org.xbill.DNS.Message;

/**
 * Extract the address from the hostname then answer.
 * Inspired at nip.io and sslip.io, see #545.
 */
public class SolverStub implements Solver {
  @Override
  public Response handle(Message query) {
    return null;
  }
}
