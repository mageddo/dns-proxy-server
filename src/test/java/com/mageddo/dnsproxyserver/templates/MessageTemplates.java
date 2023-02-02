package com.mageddo.dnsproxyserver.templates;

import lombok.SneakyThrows;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

public class MessageTemplates {
  @SneakyThrows
  public static Message buildAQuestionFor(String hostname) {
    final var r = Record.newRecord(Name.fromString(hostname), Type.A, DClass.IN, 0);
    return Message.newQuery(r);
  }

}
