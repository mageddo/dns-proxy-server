package com.mageddo.dnsproxyserver.quarkus;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.RemoteApiVersion;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.mageddo.commons.lang.Objects;
import com.mageddo.dnsproxyserver.config.Configs;

import javax.enterprise.inject.Produces;
import java.net.URI;
import java.time.Duration;

public class DockerConfig {

  @Produces
  public DockerClient dockerClient() {
    final var dockerHost = Configs
      .getInstance()
      .getDockerHost()
      ;
    final var config = DefaultDockerClientConfig.createDefaultConfigBuilder()
      .withDockerHost(Objects.mapOrNull(dockerHost, URI::toString))
      .withDockerTlsVerify(false)
      .withApiVersion(RemoteApiVersion.VERSION_1_24)
//      .withDockerCertPath("/home/user/.docker")
//      .withRegistryUsername(registryUser)
//      .withRegistryPassword(registryPass)
//      .withRegistryEmail(registryMail)
//      .withRegistryUrl(registryUrl)
      .build();

    final var httpClient = new ApacheDockerHttpClient.Builder()
      .dockerHost(config.getDockerHost())
      .sslConfig(config.getSSLConfig())
      .maxConnections(5)
      .connectionTimeout(Duration.ofMillis(300))
      .responseTimeout(Duration.ofSeconds(3))
      .build();

    return DockerClientImpl.getInstance(config, httpClient);
  }


}
