package testing.templates;

import com.mageddo.dns.utils.Messages;

import org.xbill.DNS.Flags;
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

  public static Message randomHostnameAQuery() {
    return Messages.aQuestion(System.nanoTime() + ".com");
  }

  public static Message buildAAnswerWithoutRA(Message query) {
    final var answer = buildAAnswer(query);
    answer.getHeader()
        .unsetFlag(Flags.RA);
    return answer;
  }

  public static Message stubAQueryWithoutIp() {
    return Messages.aQuestion("dps.stub");
  }

  public static Message dpsStubAQuery() {
    return Messages.aQuestion("dps-192.168.3.1.stub");
  }

  public static Message stubAQueryWithIpv6AnswerIp() {
    return Messages.aQuestion("dps.a--1.stub");
  }
}
