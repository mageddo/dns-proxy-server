package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.server.dns.Messages;
import lombok.SneakyThrows;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import static com.mageddo.dnsproxyserver.templates.HostnameTemplates.ACME_HOSTNAME;

public class MessageTemplates {

  @SneakyThrows
  public static Message buildAQuestionFor(String hostname) {
    final var r = Record.newRecord(Name.fromString(hostname), Type.A, DClass.IN, 0);
    return Message.newQuery(r);
  }

  public static Message acmeAQuery() {
    return buildAQuestionFor(ACME_HOSTNAME + ".");
  }

  public static Message buildAAnswer(Message query) {
    return Messages.aAnswer(query, IpTemplates.LOCAL);
  }

  public static Message buildNXAnswer(Message query) {
    return Messages.nxDomain(query);
  }
}
