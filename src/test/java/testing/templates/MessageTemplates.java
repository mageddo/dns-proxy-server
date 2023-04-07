package testing.templates;

import com.mageddo.dnsproxyserver.server.dns.Messages;
import org.xbill.DNS.Message;

public class MessageTemplates {

  public static Message acmeAQuery() {
    return Messages.aQuestion(HostnameTemplates.ACME_HOSTNAME + ".");
  }

  public static Message acmeQuadAQuery() {
    return Messages.quadAQuestion(HostnameTemplates.ACME_HOSTNAME + ".");
  }

  public static Message buildAAnswer(Message query) {
    return Messages.aAnswer(query, IpTemplates.LOCAL);
  }

  public static Message buildNXAnswer(Message query) {
    return Messages.nxDomain(query);
  }

  public static Message acmeAResponse() {
    return Messages.answer(MessageTemplates.acmeAQuery(), IpTemplates.LOCAL);
  }

  public static Message acmeNxDomain() {
    return Messages.nxDomain(MessageTemplates.acmeAQuery());
  }

  public static Message acmeSoaQuery() {
    return Messages.soaQuestion(HostnameTemplates.ACME_HOSTNAME);
  }
}
