package com.mageddo.dnsproxyserver.config;

import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.dataformat.v2.ConfigV2Service;
import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3Service;
import com.mageddo.dnsproxyserver.utils.Envs;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigFactory {

  private final ConfigV2Service configV2Service;
  private final ConfigV3Service configV3Service;

  public Config find() {
    if (this.isLegacyConfigActive()) {
      return this.configV2Service.findCurrentConfig();
    }
    return this.configV3Service.find();
  }

  boolean isLegacyConfigActive() {
    return Objects.requireNonNullElse(Envs.getBooleanOrNull("DPS_LEGACY_CONFIG_ACTIVE"), false);
  }
}
