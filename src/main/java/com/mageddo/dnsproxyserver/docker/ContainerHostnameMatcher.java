package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.server.dns.Hostname;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.function.Predicate;

import static com.mageddo.dnsproxyserver.docker.Docker.buildHostnamesFromServiceOrContainerNames;
import static com.mageddo.dnsproxyserver.docker.Docker.findContainerHostname;
import static com.mageddo.dnsproxyserver.docker.Docker.findHostnamesFromEnv;

public class ContainerHostnameMatcher {

  public static boolean test(final InspectContainerResponse inspect, final Hostname host, final Config config) {
    return buildPredicate(host, config).test(inspect);
  }

  static Predicate<InspectContainerResponse> buildPredicate(final Hostname host) {
    return buildPredicate(host, Configs.getInstance());
  }

  static Predicate<InspectContainerResponse> buildPredicate(final Hostname host, final Config config) {
    return container -> {

      final List<Predicate<InspectContainerResponse>> predicates = List.of(
        (it) -> host.isEqualTo(findContainerHostname(it.getConfig())),
        (it) -> findHostnamesFromEnv(it.getConfig().getEnv()).contains(host),
        (it) -> isRegisterContainerNames(config)
          && buildHostnamesFromServiceOrContainerNames(it, config.getDomain()).contains(host)
      );
      for (final var predicate : predicates) {
        if (predicate.test(container)) {
          return true;
        }
      }
      return false;
    };
  }

  static boolean isRegisterContainerNames(final Config config) {
    return BooleanUtils.isTrue(config.getRegisterContainerNames());
  }
}
