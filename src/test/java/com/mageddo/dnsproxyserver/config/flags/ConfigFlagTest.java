package com.mageddo.dnsproxyserver.config.flags;

import com.mageddo.dnsproxyserver.config.entrypoint.ConfigFlag;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.mageddo.utils.TestUtils.readAndSortJson;
import static com.mageddo.utils.TestUtils.readString;
import static com.mageddo.utils.TestUtils.sortJson;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigFlagTest {

  @Test
  void mustParseDefaultConfigs() throws Exception {

    // arrange
    final var args = new String[]{};

    // act
    final var config = ConfigFlag.parse(args);

    // assert
    assertEquals(
        readAndSortJson("/flags-test/001.json"),
        sortJson(config)
    );
  }

  @Test
  void mustPrintHelp() throws Exception {

    // arrange
    final var sw = new StringWriter();
    final var args = new String[]{"--help"};

    // act
    final var config = ConfigFlag.parse(args, new PrintWriter(sw));

    // assert
    assertEquals(
        readString("/flags-test/002.txt"),
        sw.toString()
    );

  }

  @Test
  void mustPrintVersion() throws Exception {

    // arrange
    final var sw = new StringWriter();
    final var args = new String[]{"-version"};

    // act
    final var config = ConfigFlag.parse(args, new PrintWriter(sw));

    // assert
    assertEquals(
       "${version}",
        sw.toString()
    );

  }
}
