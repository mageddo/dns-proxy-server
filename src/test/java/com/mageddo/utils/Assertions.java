package com.mageddo.utils;

import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Assertions {
  public static void validResponse(Message m) {
    assertTrue(m.getHeader()
        .getFlag(Flags.QR));
  }
}
