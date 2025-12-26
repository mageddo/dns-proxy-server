package com.mageddo.dns.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import com.mageddo.commons.lang.Objects;
import com.mageddo.dns.Hostname;
import com.mageddo.dnsproxyserver.config.Config.Entry;
import com.mageddo.dnsproxyserver.solver.HostnameQuery;
import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.utils.Ips;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
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

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Messages {

  private Messages() {
  }

  public static final long DEFAULT_TTL = 30L;
  public static final Duration DEFAULT_TTL_DURATION = Duration.ofSeconds(DEFAULT_TTL);

  public static Message authoritative(Message m) {
    setFlag(m, Flags.AA);
    return m;
  }

  private static Name findQuestionName(Message m) {
    return m.getQuestion()
        .getName();
  }

  public static String simplePrint(Response res) {
    return simplePrint(res.getMessage());
  }

  public static String simplePrint(Message reqOrRes) {
    if (reqOrRes == null) {
      return null;
    }
    try {
      final var answer = getFirstAnswer(reqOrRes);
      final var rcode = reqOrRes.getRcode();
      if (answer != null) {
        return String.format("rc=%d, res=%s", rcode, simplePrint(answer));
      }
      final var question = reqOrRes.getQuestion();
      final var type = Objects.useItOrDefault(
          Objects.toString(Entry.Type.of(question.getType())),
          () -> String.valueOf(question.getType())
      );
      final var hostname = question.getName()
          .toString(true);
      final var sb = new StringBuilder();
      if (Messages.hasFlag(reqOrRes, Flags.QR)) {
        sb.append("rc=")
            .append(rcode)
            .append(", ")
        ;
      }
      sb.append(String.format("query=%s:%s", type, hostname));
      return sb.toString();
    } catch (Throwable e) {
      log.warn("status=failedToSimplePrint, msg={}", reqOrRes, e);
      return String.valueOf(reqOrRes);
    }
  }

  public static String detailedPrint(Message msg) {
    final var sb = new StringBuilder();
    for (final var record : msg.getSection(1)) {
      sb.append(simplePrint(record));
      sb.append(" | ");
    }
    if (sb.length() > 3) {
      sb.delete(sb.length() - 3, sb.length());
    }
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

  public static Message aAnswer(Message query, String ip, long ttl) {
    final var res = withNoErrorResponse(copy(query));
    if (StringUtils.isBlank(ip)) {
      return res;
    }
    final var answer = new ARecord(res.getQuestion()
        .getName(), DClass.IN, ttl, Ips.toAddress(ip)
    );
    res.addRecord(answer, Section.ANSWER);
    return res;
  }

  public static String findFirstAnswerRecordStr(Message msg) {
    final var v = getFirstAnswer(msg);
    return v == null ? null : v.toString();
  }

  public static Record findFirstAuthorityRecord(Message msg) {
    return getFirstRecord(msg, Section.AUTHORITY);
  }

  public static Record getFirstRecord(Message msg, final int sectionType) {
    final var section = msg.getSection(sectionType);
    if (section.isEmpty()) {
      return null;
    }
    return section.getFirst();
  }

  public static Message aQuestion(String host) {
    return Message.newQuery(query(host, Type.A));
  }

  public static Message quadAQuestion(String host) {
    return Message.newQuery(query(host, Type.AAAA));
  }

  public static Message soaQuestion(String hostname) {
    return Message.newQuery(query(hostname, Type.SOA));
  }

  @SneakyThrows
  public static Record query(final String host, final int type) {
    return Record.newRecord(Name.fromString(Hostnames.toAbsoluteName(host)), type, DClass.IN, 0);
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
    final var clone = copy(target);
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
    newMsg.getHeader()
        .setID(msg.getHeader()
            .getID());
    return newMsg;
  }

  public static Duration findTTL(Message m) {
    final var answer = Optional
        .ofNullable(Messages.getFirstAnswer(m))
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
    final var reqId = req.getHeader()
        .getID();
    res.getHeader()
        .setID(reqId);
    return res;
  }

  public static Message nxDomain(Message query) {
    return withResponseCode(query.clone(), Rcode.NXDOMAIN);
  }

  @SneakyThrows
  public static Message cnameResponse(Message query, Integer ttl, String hostname) {
    final var res = withNoErrorResponse(copy(query));
    final var answer = new CNAMERecord(
        res.getQuestion()
            .getName(),
        DClass.IN, ttl,
        Name.fromString(Hostnames.toAbsoluteName(hostname))
    );
    res.addRecord(answer, Section.ANSWER);
    return res;
  }

  public static Message quadAnswer(Message query, String ip) {
    return quadAnswer(query, ip, DEFAULT_TTL);
  }

  public static Message quadAnswer(final Message query, final String ip, final long ttl) {
    final var res = withNoErrorResponse(query.clone());
    if (StringUtils.isBlank(ip)) {
      return res;
    }
    final var answer = new AAAARecord(res.getQuestion()
        .getName(), DClass.IN, ttl, Ips.toAddress(ip)
    );
    res.addRecord(answer, Section.ANSWER);
    return res;
  }

  public static Message answer(Message query, String ip) {
    if (Ips.isIpv6(ip)) {
      return Messages.quadAnswer(query, ip);
    }
    return Messages.aAnswer(query, ip);
  }

  public static Message answer(Message query, String ip, Entry.Type type) {
    return answer(query, ip, type, DEFAULT_TTL);
  }

  public static Message answer(Message query, String ip, Entry.Type type, long ttl) {
    Validate.notNull(type, "type must not be null, query=%s", toHostnameQuery(query));
    return switch (type) {
      case A -> Messages.aAnswer(query, ip, ttl);
      case AAAA -> Messages.quadAnswer(query, ip, ttl);
      default -> throw new UnsupportedOperationException(String.valueOf(type));
    };
  }

  static Message withNoErrorResponse(Message res) {
    return withResponseCode(res, Rcode.NOERROR);
  }

  public static Message withResponseCode(Message res, int rRode) {
    withDefaultResponseHeaders(res);
    res.getHeader()
        .setRcode(rRode);
    return res;
  }

  public static int getRCode(Message m) {
    return m.getRcode();
  }

  public static Message withDefaultResponseHeaders(Message res) {
    final var header = res.getHeader();
    header.setFlag(Flags.QR);
    header.setFlag(Flags.RA);
    return res;
  }

  public static Message copy(Message msg) {
    if (msg == null) {
      return null;
    }
    return msg.clone();
  }

  public static Message setFlag(Message m, int flag) {
    m.getHeader()
        .setFlag(flag);
    return m;
  }

  public static Message unsetFlag(Message m, int flag) {
    m.getHeader()
        .unsetFlag(flag);
    return m;
  }

  public static boolean hasFlag(Message msg, int flag) {
    return msg.getHeader()
        .getFlag(flag);
  }

  public static HostnameQuery toHostnameQuery(Message query) {
    final var host = Messages.findQuestionHostname(query);
    final var version = Entry.Type.of(findQuestionTypeCode(query))
        .toVersion();
    return HostnameQuery.of(host, version);
  }

  public static boolean isSuccess(Message res) {
    return res.getRcode() == Rcode.NOERROR;
  }

  public static String findAnswerRawIP(Message res) {
    return getFirstAnswer(res).rdataToString();
  }

  public static boolean isNxDomain(Message m) {
    return m.getRcode() == Rcode.NXDOMAIN;
  }

  public static Message noData(Message query) {
    return withResponseCode(query.clone(), Rcode.NOERROR);
  }

  public static Message of(byte[] m) {
    try {
      return new Message(m);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Message unsetAuthoritative(Message m) {
    return unsetFlag(m, Flags.AA);
  }

  public static Message authoritativeAnswer(Message query, String ip, Entry.Type type) {
    return authoritative(answer(query, ip, type));
  }

  public static Message authoritativeAnswer(
      Message query, String ip, Entry.Type type, long ttl
  ) {
    return authoritative(answer(query, ip, type, ttl));
  }

  public static boolean isAuthoritative(Message m) {
    return Messages.hasFlag(m, Flags.AA);
  }

  public static boolean isRecursionAvailable(Message m) {
    return hasFlag(m, Flags.RA);
  }

  public static int getId(Message m) {
    return m.getHeader()
        .getID();
  }

  public static Message notSupportedHttps(Message m) {
    return authoritative(withNoErrorResponse(copy(m)));
  }

  public static List<Record> getAnswers(Message m) {
    return m.getSection(Section.ANSWER);
  }

  public static long getFirstAnswerTTL(Response res) {
    return getFirstAnswerTTL(res.getMessage());
  }

  public static long getFirstAnswerTTL(Message message) {
    return Objects.mapOrNull(getFirstAnswer(message), Record::getTTL);
  }

  public static Record getFirstAnswer(Message message) {
    return getFirstRecord(message, Section.ANSWER);
  }

  public static Duration getFirstAnswerTTLDuration(Response response) {
    final var ttl = getFirstAnswerTTL(response);
    return Objects.mapOrNull(ttl, Duration::ofSeconds);
  }
}
