package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.utils.Ips;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Section;

public class Messages {
  public static String simplePrint(Message message) {
    return message
      .toString()
      .substring(0, 10);
  }

  public static Hostname findQuestionHostname(Message m) {
    final var hostname = m
      .getQuestion()
      .getName()
      .toString(true);
    return Hostname.of(hostname);
  }

  public static Message aAnswer(Message reqMsg, String ip) {
    final var res = new Message(reqMsg.getHeader().getID());
//     = Record.newRecord(reqMsg.getQuestion().getName(), Type.A, DClass.IN, 30, Ips.toBytes(ip));
    final var answer = new ARecord(reqMsg.getQuestion().getName(), DClass.IN, 30L, Ips.toAddress(ip));
    res.addRecord(answer, Section.ANSWER);
    return res;
  }
}
