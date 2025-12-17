package testing.templates.solver.remote;

import com.mageddo.dnsproxyserver.solver.remote.Request;
import com.mageddo.dnsproxyserver.solver.remote.mapper.ResolverMapper;

import org.apache.commons.lang3.time.StopWatch;

import testing.templates.InetSocketAddressTemplates;
import testing.templates.MessageTemplates;

public class RequestTemplates {
  public static Request buildDefault() {
    final var stopWatch = StopWatch.createStarted();
    stopWatch.split();
    return Request
        .builder()
        .query(MessageTemplates.acmeAQuery())
        .resolver(ResolverMapper.from(InetSocketAddressTemplates._8_8_8_8()))
        .resolverIndex(0)
        .stopWatch(stopWatch)
        .build();
  }
}
