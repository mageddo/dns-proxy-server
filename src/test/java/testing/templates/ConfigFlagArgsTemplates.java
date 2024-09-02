package testing.templates;

import com.mageddo.net.SocketUtils;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigFlagArgsTemplates {
  public static String[] withRandomPortsAndNotAsDefaultDns() {
    final var webServerPort = SocketUtils.findRandomFreePort();
    final var dnsServerPort = SocketUtils.findRandomFreePort();

    return new String[]{
      "--default-dns=false",
      "--web-server-port=" + webServerPort,
      "--server-port=" + dnsServerPort,
      "--log-level=TRACE",
    };
  }

  public static String[] withRandomPortsAndNotAsDefaultDnsAndCustomLocalDBEntry(String host) {
    final var configPath = makeConfigFile(host);
    final var webServerPort = SocketUtils.findRandomFreePort();
    final var dnsServerPort = SocketUtils.findRandomFreePort();
    return new String[]{
      "--default-dns=false",
      "--web-server-port=" + webServerPort,
      "--server-port=" + dnsServerPort,
      "--log-level=TRACE",
      "--conf-path=" + configPath.toString()
    };
  }

  @SneakyThrows
  private static Path makeConfigFile(String host) {
    final var configJsonContent = """
      {
        "version": 2,
        "remoteDnsServers": [],
        "envs": [
          {
            "name": "",
            "hostnames":
              {
                "type": "A",
                "hostname": "%s",
                "ip": "192.168.0.1",
                "ttl": 255
              }
            ]
          }
        ]
      }
      """.formatted(host);
    final var config = Files.createTempFile("config", ".json");
    return Files.writeString(config, configJsonContent);
  }
}
