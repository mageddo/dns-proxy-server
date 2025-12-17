package testing.templates.docker;

import java.util.List;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.Solver;

public class SolverTemplates {
  public static List<Solver> mockTo192() {
    return List.of(reqMsg -> {
      return Response.internalSuccess(Messages.aAnswer(reqMsg, "192.168.1.8"));
    });
  }
}
