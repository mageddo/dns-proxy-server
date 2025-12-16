package testing.templates;

import com.mageddo.dnsproxyserver.config.dataformat.v2.cmdargs.vo.ConfigFlag;

import java.nio.file.Path;

public class ConfigFlagTemplates {
  public static ConfigFlag build() {
    return ConfigFlag.parse(new String[]{});
  }

  public static ConfigFlag defaultWithConfigPath(Path path) {
    return ConfigFlag.parse(new String[]{"--conf-path", path.toString()});
  }

  public static ConfigFlag withHelpFlag(){
    return ConfigFlag.parse(new String[]{"--help"});
  }

  public static ConfigFlag withVersionFlag() {
    return ConfigFlag.parse(new String[]{"--version"});
  }

  public static ConfigFlag withConfigFilePath() {
    return ConfigFlag.parse(ConfigFlagArgsTemplates.withConfigFilePath());
  }

  public static ConfigFlag empty() {
    return ConfigFlag.parse(ConfigFlagArgsTemplates.empty());
  }

  public static ConfigFlag withStubSolverDomainName() {
    return build();
  }
}
