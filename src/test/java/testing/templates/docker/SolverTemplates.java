package testing.templates.docker;

import com.mageddo.dns.Messages;
import com.mageddo.dnsproxyserver.server.dns.solver.Response;
import com.mageddo.dnsproxyserver.server.dns.solver.Solver;

import java.util.List;

public class SolverTemplates {
  public static List<Solver> mockTo192() {
    return List.of(reqMsg -> {
      return Response.internalSuccess(Messages.aAnswer(reqMsg, "192.168.1.8"));
    });
  }
}
