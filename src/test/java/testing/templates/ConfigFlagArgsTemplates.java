package testing.templates;

import com.mageddo.dnsproxyserver.config.dataprovider.JsonConfigs;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJson;
import com.mageddo.net.IpAddr;
import com.mageddo.net.SocketUtils;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

  public static Config withRandomPortsAndNotAsDefaultDnsAndCustomLocalDBEntry(String host) {
    final var configPath = makeConfigFileRandomPortAndCustomLocalEntry(host);
    final var args = new String[]{
      "--conf-path=" + configPath.toString()
    };
    return Config.builder()
      .args(args)
      .config(JsonConfigs.loadConfig(configPath))
      .build();
  }

  @SneakyThrows
  private static Path makeConfigFileRandomPortAndCustomLocalEntry(String host) {
    final var webServerPort = SocketUtils.findRandomFreePort();
    final var dnsServerPort = SocketUtils.findRandomFreePort();
    final var configJsonContent = """
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
      """.formatted(webServerPort, dnsServerPort, host);
    return writeToTempPath(configJsonContent);
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
        "envs": []
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

    public Integer getDnsServerPort() {
      return config().getDnsServerPort();
    }
  }
}
