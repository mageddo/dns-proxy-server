package testing.templates;

import com.mageddo.net.SocketUtils;

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

  public static String[] withConfigFilePath() {
    return new String[]{
      "--conf-path=flag-relative-path/flag-config.json"
    };
  }

  public static String[] empty() {
    return new String[]{};
  }
}
