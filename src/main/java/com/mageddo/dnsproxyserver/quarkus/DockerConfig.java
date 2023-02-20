package com.mageddo.dnsproxyserver.quarkus;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;

import javax.enterprise.inject.Produces;
import java.net.URI;
import java.time.Duration;

public class DockerConfig {

  public static final URI DOCKER_HOST_ADDRESS = URI.create("unix:///var/run/docker.sock");

  @Produces
  public DockerClient dockerClient() {
    final var config = DefaultDockerClientConfig.createDefaultConfigBuilder()
      .withDockerHost(DOCKER_HOST_ADDRESS.toString())
      .withDockerTlsVerify(false)
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
