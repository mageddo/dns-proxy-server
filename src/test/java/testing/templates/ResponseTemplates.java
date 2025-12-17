package testing.templates;

import java.time.Duration;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.solver.Response;

import org.xbill.DNS.Message;

public class ResponseTemplates {
  public static Response to(Message query) {
    return Response.of(Messages.aAnswer(query, "0.0.0.0"), Duration.ofSeconds(5));
  }

  public static Response acmeAResponse() {
    return to(MessageTemplates.acmeAResponse());
  }

  public static Response acmeNxDomain() {
    return to(MessageTemplates.acmeNxDomain());
  }
}
