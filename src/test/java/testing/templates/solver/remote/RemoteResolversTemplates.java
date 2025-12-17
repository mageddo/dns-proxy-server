package testing.templates.solver.remote;

import java.util.List;

import com.mageddo.dnsproxyserver.solver.Resolver;

public class RemoteResolversTemplates {

  public static List<Resolver> buildSuccessAnswerResolverStub() {
    return List.of(ResolverTemplates.successAAcmeAnswer());
  }
}
