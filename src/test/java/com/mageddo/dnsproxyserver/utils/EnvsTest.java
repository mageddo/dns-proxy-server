package com.mageddo.dnsproxyserver.utils;

import org.junit.jupiter.api.Test;

import static com.mageddo.dnsproxyserver.utils.Booleans.parse;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnvsTest {

  @Test
  void mustParseAsTrue() {
    // arrange

    // act
    // assert
    assertTrue(parse("true"));
    assertTrue(parse("TRUE"));
    assertTrue(parse("TRuE"));
    assertTrue(parse("1"));
  }

  @Test
  void mustParseAsFalse() {
    // arrange

    // act
    // assert
    assertFalse(parse("dps"));
    assertFalse(parse("0"));
    assertFalse(parse("!"));
    assertFalse(parse("FALSE"));
    assertFalse(parse("false"));
  }

  @Test
  void mustParseAsNull() {
    // arrange

    // act
    // assert
    assertNull(parse(""));
  }
}
