package com.mageddo.dnsproxyserver.utils;

import org.junit.jupiter.api.Test;

import static com.mageddo.dnsproxyserver.utils.Envs.parseBoolean;
import static org.junit.jupiter.api.Assertions.*;

class EnvsTest {

  @Test
  void mustParseAsTrue(){
    // arrange

    // act
    // assert
    assertTrue(parseBoolean("true"));
    assertTrue(parseBoolean("TRUE"));
    assertTrue(parseBoolean("TRuE"));
    assertTrue(parseBoolean("1"));
  }

  @Test
  void mustParseAsFalse(){
    // arrange

    // act
    // assert
    assertFalse(parseBoolean("dps"));
    assertFalse(parseBoolean("0"));
    assertFalse(parseBoolean("!"));
    assertFalse(parseBoolean("FALSE"));
    assertFalse(parseBoolean("false"));
  }

  @Test
  void mustParseAsNull(){
    // arrange

    // act
    // assert
    assertNull(parseBoolean(""));
  }
}
