package com.mageddo.dnsproxyserver.solver.stub.addressexpression;

import com.mageddo.net.IP;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class HexadecimalParser implements Parser {
  @Override
  public IP parse(String addressExpression) {
    try {
      return IP.of(Hex.decodeHex(addressExpression));
    } catch (DecoderException e) {
      throw new ParseException("not a hexadecimal address: " + addressExpression, e);
    }
  }
}
