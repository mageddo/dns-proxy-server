package com.mageddo.utils.templates;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.mageddo.json.JsonUtils;
import com.mageddo.utils.TestUtils;
import lombok.SneakyThrows;

public class InspectContainerResponseTemplates {

  public static InspectContainerResponse buildWithHostnameAndWithoutDomain() {
    return build();
  }

  public static InspectContainerResponse buildWithHostnameAndDomain(String hostname, String domain) {
    final var tree = buildTree();
    final var config = (ObjectNode) tree.at("/Config");
    config.put("Hostname", hostname);
    config.put("Domainname", domain);
    return parse(tree);
  }

  @SneakyThrows
  public static InspectContainerResponse build() {
    return parse(buildTree());
  }

  @SneakyThrows
  private static InspectContainerResponse parse(final ObjectNode tree) {
    return JsonUtils
      .instance()
      .treeToValue(tree, InspectContainerResponse.class);
  }

  static ObjectNode buildTree() {
    return (ObjectNode) JsonUtils.readTree(TestUtils.readAsStream("/templates/nginx.json"));
  }

}
