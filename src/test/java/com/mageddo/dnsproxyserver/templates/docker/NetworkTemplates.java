package com.mageddo.dnsproxyserver.templates.docker;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.dockerjava.api.model.Network;
import com.mageddo.json.JsonUtils;
import lombok.SneakyThrows;

public class NetworkTemplates {

  @SneakyThrows
  public static Network withBridgeDriver(String name) {
    final var node = JsonNodeFactory.instance.objectNode()
      .put("Name", name)
      .put("Driver", "bridge");
    return JsonUtils
      .instance()
      .treeToValue(node, Network.class)
      ;
  }

  @SneakyThrows
  public static Object withOverlayDriver(String name) {
    final var node = JsonNodeFactory.instance.objectNode()
      .put("Name", name)
      .put("Driver", "overlay");
    return JsonUtils
      .instance()
      .treeToValue(node, Network.class)
      ;
  }
}
