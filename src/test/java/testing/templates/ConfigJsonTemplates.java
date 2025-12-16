package testing.templates;

import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.dataprovider.JsonConfigs;
import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.vo.ConfigJson;
import com.mageddo.utils.TestUtils;

public class ConfigJsonTemplates {

  public static ConfigJson withNoRemoteServersAndCircuitBreakerDefined() {
    final var path = "/configs-test/005.json";
    return JsonConfigs.loadConfig(TestUtils.readString(path));
  }

  public static ConfigJson withoutCircuitBreakerDefinedWithNoRemoteServers() {
    final var path = "/configs-test/007.json";
    return JsonConfigs.loadConfig(TestUtils.readString(path));
  }

  public static ConfigJson noRemoteServerFlagsSet() {
    final var path = "/configs-test/008.json";
    return JsonConfigs.loadConfig(TestUtils.readString(path));
  }

  public static ConfigJson canaryRateThresholdCircuitBreaker() {
    final var path = "/configs-test/009.json";
    return JsonConfigs.loadConfig(TestUtils.readString(path));
  }

  public static ConfigJson withDnsServers() {
    final var path = "/configs-test/011.json";
    return JsonConfigs.loadConfig(TestUtils.readString(path));
  }
}
