package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.utils.Ips;
import lombok.SneakyThrows;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

import java.util.Optional;

public class Messages {

  public static String simplePrint(Message message) {
   final var answer = findFirstAnswerRecord(message);
    if (answer == null) {
      return Optional
          .ofNullable(findQuestionHostname(message))
          .map(Hostname::getName)
          .orElse("N/A");
    }
    return String.format("%s", simplePrint(answer));
  }

  public static String simplePrint(Record r) {
    if (r == null) {
      return null;
    }
    return r
        .toString()
        .replaceAll("\\t", "  ")
        ;
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

  public static Message aAnswer(Message msg, String ip) {
    return aAnswer(msg, ip, 30L);
  }

  public static Message aAnswer(Message msg, String ip, final long ttl) {
    msg.getHeader().setRcode(Rcode.NOERROR);
    final var answer = new ARecord(msg.getQuestion().getName(), DClass.IN, ttl, Ips.toAddress(ip));
    msg.addRecord(answer, Section.ANSWER);
    return msg;
  }

  public static String findFirstAnswerRecordStr(Message msg) {
    final var v = findFirstAnswerRecord(msg);
    return v == null ? null : v.toString();
  }

  public static Record findFirstAnswerRecord(Message msg) {
    final var section = msg.getSection(1);
    if (section.isEmpty()) {
      return null;
    }
    return section.get(0);
  }

  public static Message nxDomain(Message msg) {
    msg.getHeader().setRcode(Rcode.NXDOMAIN);
    return msg;
  }

  public static Message answer(Message msg, Config.Entry entry) {
    if (entry.getType() == Config.Entry.Type.A) {
      return aAnswer(msg, entry.getIp(), entry.getTtl());
    }
    return cnameAnswer(msg, entry);
  }

  public static Message cnameAnswer(Message msg, Config.Entry entry) {
    return cnameAnswer(msg, entry.getTtl(), entry.getTarget());
  }

  @SneakyThrows
  public static Message cnameAnswer(Message msg, Integer ttl, String hostname) {
    msg.getHeader().setRcode(Rcode.NOERROR);
    final var answer = new CNAMERecord(
        msg.getQuestion().getName(),
        DClass.IN, ttl,
        Name.fromString(Hostnames.toAbsoluteName(hostname))
    );
    msg.addRecord(answer, Section.ANSWER);
    return msg;
  }

  @SneakyThrows
  public static Message aQuestion(String host) {
    final var msg = new Message();
    final var q = Record.newRecord(Name.fromString(host), org.xbill.DNS.Type.A, DClass.IN, 0);
    msg.addRecord(q, Section.QUESTION);
    return msg;
  }
}
