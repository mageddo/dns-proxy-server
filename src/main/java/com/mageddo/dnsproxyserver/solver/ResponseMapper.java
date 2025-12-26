package com.mageddo.dnsproxyserver.solver;

import com.mageddo.commons.lang.Objects;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.net.IP;

import org.xbill.DNS.Message;

public class ResponseMapper {
  public static Response toDefaultSuccessAnswer(Message query, IP ip, Config.Entry.Type type) {
    return Response.of(
        Messages.authoritativeAnswer(query, Objects.mapOrNull(ip, IP::toText), type),
        Messages.DEFAULT_TTL_DURATION
    );
  }
}
