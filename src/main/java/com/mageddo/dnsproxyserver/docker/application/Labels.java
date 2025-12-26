package com.mageddo.dnsproxyserver.docker.application;

import java.util.Map;
import java.util.Objects;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerConfig;
import com.mageddo.dnsproxyserver.utils.Booleans;

public class Labels {

  public static final String SERVICE_NAME_LABEL = "com.docker.compose.service";

  public static String findValue(Container c, String label) {
    if (c == null) {
      return null;
    }
    return findValue(c.getLabels(), label);
  }

  public static boolean findBoolean(Container c, String label) {
    return findBoolean(c, label, false);
  }

  public static boolean findBoolean(Container c, String label, boolean defaultValue) {
    return Objects.requireNonNullElse(
        Booleans.parse(findValue(c, label)),
        defaultValue
    );
  }

  public static String findValue(ContainerConfig c, String label) {
    return findValue(c.getLabels(), label);
  }

  public static String findValue(InspectContainerResponse inspect, String label) {
    return findValue(inspect.getConfig(), label);
  }

  public static String findValue(Map<String, String> labels, String label) {
    if (labels == null) {
      return null;
    }
    return labels.get(label);
  }

}
