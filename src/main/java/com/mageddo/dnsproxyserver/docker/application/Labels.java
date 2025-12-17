package com.mageddo.dnsproxyserver.docker.application;

import java.util.Map;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ContainerConfig;

public class Labels {

  public static final String SERVICE_NAME_LABEL = "com.docker.compose.service";

  public static String findLabelValue(ContainerConfig c, String label) {
    return findLabelValue(c.getLabels(), label);
  }

  public static String findLabelValue(InspectContainerResponse inspect, String label) {
    return findLabelValue(inspect.getConfig(), label);
  }

  public static String findLabelValue(Map<String, String> labels, String label) {
    if (labels == null) {
      return null;
    }
    return labels.get(label);
  }

}
