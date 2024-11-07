package com.mageddo.dnsproxyserver.solver;

import com.mageddo.commons.lang.Objects;
import com.mageddo.dns.utils.Messages;
import com.mageddo.net.IP;
import org.xbill.DNS.Message;

public class ResponseMapper {
  public static Response toDefaultSuccessAnswer(Message query, IP ip) {
    return Response.of(
      Messages.answer(query, Objects.mapOrNull(ip, IP::toText), ip.version()),
      Messages.DEFAULT_TTL_DURATION
    );
  }
}
