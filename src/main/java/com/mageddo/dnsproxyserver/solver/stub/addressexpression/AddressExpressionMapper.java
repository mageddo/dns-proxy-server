package com.mageddo.dnsproxyserver.solver.stub.addressexpression;

import com.mageddo.net.IP;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UncheckedIOException;

public class AddressExpressionMapper {

  public static IP toAddr(String addressExpression) {
    final var ipv6Parser = new Ipv6Parser();
    try {
      return ipv6Parser.parse(addressExpression);
    } catch (CantParseException e) {

    }

    final var normalizedStr = addressExpression.replaceAll("-", ".");
    if (StringUtils.countMatches(normalizedStr, '.') == IP.IPV4_BYTES - 1) {
      return IP.of(normalizedStr);
    }

    try {
      return IP.of(Hex.decodeHex(addressExpression));
    } catch (DecoderException e) {
      throw new UncheckedIOException(new IOException(e));
    }
  }

}
