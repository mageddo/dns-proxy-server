package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.utils.Ips;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Section;

import java.util.Optional;

public class Messages {
  public static String simplePrint(Message message) {
    return Optional
      .ofNullable(findQuestionHostname(message))
      .map(Hostname::getName)
      .orElse("N/A");
  }

  public static Hostname findQuestionHostname(Message m) {
    final var question = m.getQuestion();
    if (question == null) {
      return null;
    }
    final var hostname = question
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

  public static String findFirstAnswerRecord(Message msg) {
    final var section = msg.getSection(1);
    if (section.isEmpty()) {
      return null;
    }
    return section.get(0).toString();
  }

  public static Message nxDomain(Message msg) {
    msg.getHeader().setRcode(Rcode.NXDOMAIN);
    return msg;
  }

  public static Message aAnswer(Message reqMsg, Config.Entry entry) {
    return null;
  }
}
