package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.config.Config.Entry;
import com.mageddo.dnsproxyserver.server.dns.solver.Response;
import com.mageddo.dnsproxyserver.utils.Ips;
import lombok.SneakyThrows;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

import java.time.Duration;
import java.util.Optional;

public class Messages {

  private Messages() {
  }

  public static final long DEFAULT_TTL = 30L;
  public static final Duration DEFAULT_TTL_DURATION = Duration.ofSeconds(DEFAULT_TTL);

  public static String simplePrint(Response res) {
    return simplePrint(res.getMessage());
  }

  public static String simplePrint(Message message) {
    if (message == null) {
      return null;
    }
    final var answer = findFirstAnswerRecord(message);
    if (answer == null) {
      return Optional
        .ofNullable(findQuestionHostname(message))
        .map(Hostname::getValue)
        .orElse("N/A");
    }
    return String.format("%s", simplePrint(answer));
  }

  public static String detailedPrint(Message msg) {
    final var sb = new StringBuilder();
    for (final var record : msg.getSection(1)) {
      sb.append(simplePrint(record));
      sb.append(" | ");
    }
    sb.delete(sb.length() - 3, sb.length());
    return sb.toString();
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

  public static Message aAnswer(Message query, String ip) {
    return aAnswer(query, ip, DEFAULT_TTL);
  }

  public static Message aAnswer(Message query, String ip, final long ttl) {
    final var res = withNoErrorResponse(query.clone());
    final var answer = new ARecord(res.getQuestion().getName(), DClass.IN, ttl, Ips.toAddress(ip));
    res.addRecord(answer, Section.ANSWER);
    return res;
  }

  public static String findFirstAnswerRecordStr(Message msg) {
    final var v = findFirstAnswerRecord(msg);
    return v == null ? null : v.toString();
  }

  public static Record findFirstAnswerRecord(Message msg) {
    return getFirstRecord(msg, Section.ANSWER);
  }

  public static Record findFirstAuthorityRecord(Message msg) {
    return getFirstRecord(msg, Section.AUTHORITY);
  }

  public static Record getFirstRecord(Message msg, final int sectionType) {
    final var section = msg.getSection(sectionType);
    if (section.isEmpty()) {
      return null;
    }
    return section.get(0);
  }

  public static Message aQuestion(String host) {
    return Message.newQuery(query(host, Type.A));
  }

  public static Message quadAQuestion(String host) {
    return Message.newQuery(query(host, Type.AAAA));
  }

  @SneakyThrows
  public static Record query(String host, final int type) {
    return Record.newRecord(Name.fromString(host), type, DClass.IN, 0);
  }

  public static Integer findQuestionTypeCode(Message msg) {
    return Optional
      .ofNullable(msg.getQuestion())
      .map(Record::getType)
      .orElse(null)
      ;
  }

  public static Entry.Type findQuestionType(Message msg) {
    return Entry.Type.of(findQuestionTypeCode(msg));
  }

  /**
   * Add records from source to target for all sections
   *
   * @return a clone with the combination.
   */
  public static Message combine(Message source, Message target) {
    final var clone = clone(target);
    for (int i = 1; i < 4; i++) {
      final var section = source.getSection(i);
      for (final var record : section) {
        clone.addRecord(record, i);
      }
    }
    return clone;
  }

  @SneakyThrows
  public static Message copyQuestionForNowHostname(Message msg, String hostname) {
    final var newMsg = Message.newQuery(msg
      .getQuestion()
      .withName(Name.fromString(hostname))
    );
    newMsg.getHeader().setID(msg.getHeader().getID());
    return newMsg;
  }

  public static Duration findTTL(Message m) {
    final var answer = Optional
      .ofNullable(Messages.findFirstAnswerRecord(m))
      .orElseGet(() -> Messages.findFirstAuthorityRecord(m));
    if (answer == null) {
      return Duration.ZERO;
    }
    return Duration.ofSeconds(answer.getTTL());
  }

  /**
   * Set the id of the query into the response, se the response will match if the query;
   */
  public static Message mergeId(Message req, Message res) {
    final var reqId = req.getHeader().getID();
    res.getHeader().setID(reqId);
    return res;
  }

  public static Message nxDomain(Message query) {
    return withResponseCode(query.clone(), Rcode.NXDOMAIN);
  }

  @SneakyThrows
  public static Message cnameResponse(Message query, Integer ttl, String hostname) {
    final var res = withNoErrorResponse(query.clone());
    final var answer = new CNAMERecord(
      res.getQuestion().getName(),
      DClass.IN, ttl,
      Name.fromString(Hostnames.toAbsoluteName(hostname))
    );
    res.addRecord(answer, Section.ANSWER);
    return res;
  }

  public static Message quadAnswer(Message query, String ip) {
    return quadAnswer(query, ip, DEFAULT_TTL);
  }

  public static Message quadAnswer(Message query, String ip, final long ttl) {
    final var res = withNoErrorResponse(query.clone());
    final var answer = new AAAARecord(res.getQuestion().getName(), DClass.IN, ttl, Ips.toAddress(ip));
    res.addRecord(answer, Section.ANSWER);
    return res;
  }

  public static Message answer(Message query, String ip) {
    if (Ips.isIpv6(ip)) {
      return Messages.quadAnswer(query, ip);
    }
    return Messages.aAnswer(query, ip);
  }

  public static Message answer(Message query, String ip, IP.Version version) {
    if (version.isIpv6()) {
      return Messages.quadAnswer(query, ip);
    }
    return Messages.aAnswer(query, ip);
  }

  static Message withNoErrorResponse(Message res) {
    return withResponseCode(res, Rcode.NOERROR);
  }

  static Message withResponseCode(Message res, int rRode) {
    withDefaultResponseHeaders(res);
    res.getHeader().setRcode(rRode);
    return res;
  }

  static Message withDefaultResponseHeaders(Message res) {
    final var header = res.getHeader();
    header.setFlag(Flags.QR);
    header.setFlag(Flags.RA);
    return res;
  }

  static Message clone(Message msg) {
    if (msg == null) {
      return null;
    }
    return msg.clone();
  }

  public static Message setFlag(Message m, int flag) {
    m.getHeader().setFlag(flag);
    return m;
  }

  public static boolean hasFlag(Message msg, int flag) {
    return msg.getHeader().getFlag(flag);
  }
}
