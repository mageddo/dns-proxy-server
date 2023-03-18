package com.mageddo.dnsproxyserver.templates.docker;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.mageddo.json.JsonUtils;
import com.mageddo.utils.TestUtils;
import lombok.SneakyThrows;

public class InspectContainerResponseTemplates {

  public static final String NGINX = "/templates/nginx.json";

  public static InspectContainerResponse buildWithHostnameAndWithoutDomain() {
    return build();
  }

  public static InspectContainerResponse buildWithHostnameAndDomain(String hostname, String domain) {
    final var tree = buildTree(NGINX);
    final var config = (ObjectNode) tree.at("/Config");
    config.put("Hostname", hostname);
    config.put("Domainname", domain);
    return parse(tree);
  }
  public static InspectContainerResponse buildWithHostnamesEnv(String hostname) {
    final var tree = buildTree(NGINX);
    final var config = (ObjectNode) tree.at("/Config");
    config.putArray("Env").add("HOSTNAMES=" + hostname);
    return parse(tree);
  }

  public static InspectContainerResponse withDpsLabel() {
    return build();
  }

  public static InspectContainerResponse withCustomBridgeAndOverylayNetwork() {
    return parse(buildTree("/templates/002.json"));
  }

  @SneakyThrows
  public static InspectContainerResponse build() {
    return parse();
  }

  private static InspectContainerResponse parse() {
    return parse(buildTree(NGINX));
  }

  @SneakyThrows
  private static InspectContainerResponse parse(final ObjectNode tree) {
    return JsonUtils
      .instance()
      .treeToValue(tree, InspectContainerResponse.class);
  }

  static ObjectNode buildTree(final String path) {
    return (ObjectNode) JsonUtils.readTree(TestUtils.readAsStream(path));
  }

}
