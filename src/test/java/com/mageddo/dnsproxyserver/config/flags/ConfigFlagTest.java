package com.mageddo.dnsproxyserver.config.flags;

import com.mageddo.commons.regex.Regexes;
import com.mageddo.dnsproxyserver.config.entrypoint.ConfigFlag;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Pattern;

import static com.mageddo.utils.TestUtils.readAndSortJson;
import static com.mageddo.utils.TestUtils.readString;
import static com.mageddo.utils.TestUtils.sortJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
  void mustPrintHelp() {

    // arrange
    final var sw = new StringWriter();
    final var args = new String[]{"--help"};

    // act
    final var config = ConfigFlag.parse(args, new PrintWriter(sw));

    // assert
    assertEquals(
        readString("/flags-test/002.txt"),
        sw.toString().replaceAll("\r\n", "\n")
    );
    assertTrue(config.getHelp());
  }

  @Test
  void mustPrintVersion()  {

    // arrange
    final var sw = new StringWriter();
    final var args = new String[]{"-version"};

    // act
    final var config = ConfigFlag.parse(args, new PrintWriter(sw));

    // assert
    final var validVersion = Regexes.matcher(sw.toString(), Pattern.compile("\\d+\\.\\d+.\\d+.*")).matches();
    assertTrue(validVersion, sw.toString());
    assertTrue(config.isVersion());
  }
}

