package com.mageddo.dnsproxyserver.templates;

import lombok.SneakyThrows;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

public class MessageTemplates {

  public static final String ACME_HOSTNAME = "acme.com";

  @SneakyThrows
  public static Message buildAQuestionFor(String hostname) {
    final var r = Record.newRecord(Name.fromString(hostname), Type.A, DClass.IN, 0);
    return Message.newQuery(r);
  }

  public static Message acmeAQuery() {
    return buildAQuestionFor(ACME_HOSTNAME + ".");
  }


}
