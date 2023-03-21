package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.server.dns.Messages;
import org.xbill.DNS.Message;

import static com.mageddo.dnsproxyserver.templates.HostnameTemplates.ACME_HOSTNAME;

public class MessageTemplates {

  public static Message acmeAQuery() {
    return Messages.aQuestion(ACME_HOSTNAME + ".");
  }

  public static Message acmeQuadAQuery() {
    return Messages.quadAQuestion(ACME_HOSTNAME + ".");
  }

  public static Message buildAAnswer(Message query) {
    return Messages.aAnswer(query, IpTemplates.LOCAL);
  }

  public static Message buildNXAnswer(Message query) {
    return Messages.nxDomain(query);
  }
}
