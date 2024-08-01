package testing.templates.solver.remote;

import com.mageddo.dnsproxyserver.solver.Resolver;
import com.mageddo.dnsproxyserver.solver.SimpleResolver;
import testing.templates.InetSocketAddressTemplates;

import java.util.List;

public class ResolverTemplates {
  public static List<Resolver> googleDnsAsList() {
    return List.of(new SimpleResolver(InetSocketAddressTemplates._8_8_8_8()));
  }
}
