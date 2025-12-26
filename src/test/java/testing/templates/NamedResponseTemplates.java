package testing.templates;

import com.mageddo.dnsproxyserver.solver.NamedResponse;
import com.mageddo.dnsproxyserver.solver.Response;

import org.xbill.DNS.Message;

public class NamedResponseTemplates {

  public static final String UNKNOWN = "Unknown";

  public static NamedResponse of(Message message) {
    return NamedResponse.of(ResponseTemplates.to(message), UNKNOWN);
  }

  public static NamedResponse of(Response response) {
    return NamedResponse.of(response, UNKNOWN);
  }
}
