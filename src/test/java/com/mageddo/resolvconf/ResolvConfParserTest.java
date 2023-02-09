package com.mageddo.resolvconf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

class ResolvConfParserTest {

  @Test
  void mustConfigureDpsServer(@TempDir Path tmpDir) throws Exception {

    // arrrange
    final var resolvFile = Files.createTempFile(tmpDir, "resolv", ".conf");

    // act
//    ResolvConfParser.parse(resolvFile, )

    // assert

  }
}
