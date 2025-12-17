package testing.templates.solver.remote;

import java.util.List;
import java.util.concurrent.Executor;

import com.mageddo.dnsproxyserver.solver.Resolver;
import com.mageddo.dnsproxyserver.solver.SimpleResolver;

import lombok.SneakyThrows;
import testing.templates.InetSocketAddressTemplates;
import testing.templates.MessageTemplates;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

public class ResolverTemplates {
  public static List<Resolver> googleDnsAsList() {
    return List.of(new SimpleResolver(InetSocketAddressTemplates._8_8_8_8()));
  }

  @SneakyThrows
  public static Resolver successAAcmeAnswer() {
    final var resolver = new SimpleResolver();

    doReturn(MessageTemplates.acmeAResponse())
        .when(resolver)
        .send(any());
    ;
    doReturn(MessageTemplates.acmeAResponse())
        .when(resolver)
        .sendAsync(any())
    ;
    doReturn(MessageTemplates.acmeAResponse())
        .when(resolver)
        .sendAsync(any(), any(Executor.class));
    ;
    return resolver;
  }
}
