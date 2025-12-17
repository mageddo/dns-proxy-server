package com.mageddo.dnsproxyserver.di.module;

import javax.inject.Singleton;

import com.github.dockerjava.api.DockerClient;
import com.mageddo.dnsproxyserver.quarkus.DockerConfig;

import dagger.Module;
import dagger.Provides;

@Module
public interface ModuleDockerClient {

  @Provides
  @Singleton
  static DockerClient dockerClient() {
    return new DockerConfig().dockerClient();
  }

}
