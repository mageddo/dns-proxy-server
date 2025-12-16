package com.mageddo.dnsproxyserver.config.dataprovider;

import java.util.Objects;

import javax.ejb.Singleton;
import javax.inject.Inject;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v2.ConfigService;
import com.mageddo.dnsproxyserver.utils.Envs;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigDAOFactory implements ConfigDAO {

  private final ConfigService configService;

  @Override
  public Config find() {
    if (this.isLegacyConfigActive()) {
      return this.configService.findCurrentConfig();
    }
    throw new UnsupportedOperationException("Config V3 is not active yet");
  }

  boolean isLegacyConfigActive() {
    return true
        // FIXME disable legacy by default
        || Objects.requireNonNullElse(Envs.getBooleanOrNull("DPS_LEGACY_CONFIG_ACTIVE"), false);
  }
}
