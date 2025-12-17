package testing.templates;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.vo.ConfigJson;
import com.mageddo.net.IpAddr;
import com.mageddo.net.SocketUtils;

import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.Accessors;

public class ConfigFlagArgsTemplates {

  public static String[] withRandomPortsAndNotAsDefaultDnsUsingRemote(IpAddr addr) {
    final var configPath = makeConfigFileRandomPortAndCustomRemote(addr);
    return new String[]{
        "--conf-path=" + configPath.toString()
    };
  }

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

  @SneakyThrows
  private static Path makeConfigFileRandomPortAndCustomRemote(IpAddr remoteAddr) {
    final var webServerPort = SocketUtils.findRandomFreePort();
    final var dnsServerPort = SocketUtils.findRandomFreePort();
    final var configJsonContent = """
        {
          "version": 2,
          "webServerPort" : %d,
          "dnsServerPort" : %d,
          "defaultDns" : false,
          "logLevel" : "TRACE",
          "remoteDnsServers": ["%s"],
          "envs": [],
          "solverRemote" : {
            "circuitBreaker": {
              "name": "STATIC_THRESHOLD",
              "failureThreshold": 3,
              "failureThresholdCapacity": 10,
              "successThreshold": 5,
              "testDelay": "PT20S"
            }
          }
        }
        """.formatted(webServerPort, dnsServerPort, remoteAddr.toString());
    return writeToTempPath(configJsonContent);
  }

  private static Path writeToTempPath(String content) throws IOException {
    final var config = Files.createTempFile("config", ".json");
    return Files.writeString(config, content);
  }

  public static String[] withConfigFilePath() {
    return new String[]{
        "--conf-path=flag-relative-path/flag-config.json"
    };
  }

  public static String[] empty() {
    return new String[]{};
  }

  @Value
  @Builder
  @Accessors(fluent = true)
  public static class Config {

    private String[] args;
    private ConfigJson config;
    private Map<String, String> envs;

    public Integer getDnsServerPort() {
      return config().getDnsServerPort();
    }
  }
}
