package testing.templates;

import com.mageddo.net.SocketUtils;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigJsonFileTemplates {

  public static final String RANDOM_PORTS_NO_DEFAULT_CUSTOM_LOCAL_DB_ENTRY = """
    {
      "version": 2,
      "webServerPort" : %d,
      "dnsServerPort" : %d,
      "defaultDns" : false,
      "logLevel" : "TRACE",
      "remoteDnsServers": [],
      "envs": [
        {
          "name": "",
          "hostnames": [
            {
              "id" : 1,
              "type": "A",
              "hostname": "%s",
              "ip": "192.168.0.1",
              "ttl": 255
            }
          ]
        }
      ]
    }
    """;

  public static Path withRandomPortsAndNotAsDefaultDnsAndCustomLocalDBEntry(String host) {
    final var webServerPort = SocketUtils.findRandomFreePort();
    final var dnsServerPort = SocketUtils.findRandomFreePort();
    return writeToTempPathReplacing(
      RANDOM_PORTS_NO_DEFAULT_CUSTOM_LOCAL_DB_ENTRY, webServerPort, dnsServerPort, host
    );
  }

  private static Path writeToTempPathReplacing(final String jsonTemplate, Object... args) {
    return writeToTempPath(jsonTemplate.formatted(args));
  }

  @SneakyThrows
  private static Path writeToTempPath(String content) {
    final var config = Files.createTempFile("config", ".json");
    return Files.writeString(config, content);
  }
}
