package testing.templates;

import com.mageddo.net.SocketUtils;

import java.nio.file.Path;

public class ConfigFlagArgsTemplates {
  public static String[] withRandomPortsAndNotAsDefaultDns(Path dir) {
    final var webServerPort = SocketUtils.findRandomFreePort();
    final var dnsServerPort = SocketUtils.findRandomFreePort();

    return new String[]{
      "--default-dns=false",
      "--web-server-port=" + webServerPort,
      "--server-port=" + dnsServerPort,
      "--conf-path=" + dir.resolve("config.json"),
      "--log-level=TRACE",
    };
  }
}
