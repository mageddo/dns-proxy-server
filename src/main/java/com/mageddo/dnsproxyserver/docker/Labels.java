package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.model.ContainerConfig;

import java.util.Map;

public class Labels {

  public static String findLabelValue(ContainerConfig c, String label) {
    return findLabelValue(c.getLabels(), label);
  }

  public static String findLabelValue(Map<String, String> labels, String label) {
    if (labels == null) {
      return null;
    }
    return labels.get(label);
  }
}
