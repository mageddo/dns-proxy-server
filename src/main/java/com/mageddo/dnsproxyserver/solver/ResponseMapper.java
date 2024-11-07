package com.mageddo.dnsproxyserver.solver;

import com.mageddo.commons.lang.Objects;
import com.mageddo.dns.utils.Messages;
import com.mageddo.net.IP;
import org.xbill.DNS.Message;

public class ResponseMapper {
  public static Response toDefaultSuccessAnswer(Message query, IP ip, IP.Version version) {
    return Response.of(
      Messages.answer(query, Objects.mapOrNull(ip, IP::toText), version),
      Messages.DEFAULT_TTL_DURATION
    );
  }
}
