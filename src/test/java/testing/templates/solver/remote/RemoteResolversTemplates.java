package testing.templates.solver.remote;

import com.mageddo.dnsproxyserver.solver.Resolver;

import java.util.List;

public class RemoteResolversTemplates {

  public static List<Resolver> buildSuccessAnswerResolverStub() {
    return List.of(ResolverTemplates.successAAcmeAnswer());
  }
}
