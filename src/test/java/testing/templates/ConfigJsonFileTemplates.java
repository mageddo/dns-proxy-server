package testing.templates;

import com.mageddo.net.SocketUtils;
import com.mageddo.utils.TestUtils;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigJsonFileTemplates {

  public static Path withRandomPortsAndNotAsDefaultDnsAndCustomLocalDBEntry(String host) {
    final var webServerPort = SocketUtils.findRandomFreePort();
    final var dnsServerPort = SocketUtils.findRandomFreePort();
    return writeToTempPathReplacing("/configs-test/009.json", webServerPort, dnsServerPort, host);
  }

  private static Path writeToTempPathReplacing(final String resourceTemplatePath, Object ... args) {
    final var jsonTemplate = TestUtils.readString(resourceTemplatePath);
    return writeToTempPath(jsonTemplate.formatted(args));
  }

  @SneakyThrows
  private static Path writeToTempPath(String content) {
    final var config = Files.createTempFile("config", ".json");
    return Files.writeString(config, content);
  }
}
