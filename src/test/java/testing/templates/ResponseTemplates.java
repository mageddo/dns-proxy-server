package testing.templates;

import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.dnsproxyserver.server.dns.solver.Response;
import org.xbill.DNS.Message;

import java.time.Duration;

public class ResponseTemplates {
  public static Response to(Message query) {
    return Response.of(Messages.aAnswer(query, "0.0.0.0"), Duration.ofSeconds(5));
  }
}
