package testing.templates.docker;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.mageddo.json.JsonUtils;
import com.mageddo.utils.TestUtils;
import lombok.SneakyThrows;

public class InspectContainerResponseTemplates {

  /**
   * Nginx container with default bridge, dps and specific network.
   */
  public static final String NGINX = "/templates/nginx.json";

  /**
   * {@link #NGINX} container with default bridge only
   */
  public static final String NGINX_002 = "/templates/nginx-002.json";

  /**
   * Nginx container with default bridge only with ipv6 support.
   */
  public static final String NGINX_003 = "/templates/nginx-003.json";

  /**
   * Nginx container with no default bridge but a custom network with ipv6.
   */
  public static final String NGINX_004 = "/templates/nginx-004.json";

  /**
   * Nginx container with only the default ip.
   */
  public static final String NGINX_005 = "/templates/nginx-005.json";

  /**
   * Nginx container with default bridge with ipv4 address only and a custom network with ipv6.
   */
  public static final String NGINX_006 = "/templates/nginx-006.json";


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

  public static InspectContainerResponse ngixWithDefaultBridgeNetworkOnly() {
    return parse(buildTree(NGINX_002));
  }

  public static InspectContainerResponse ngixWithIpv6DefaultBridgeNetworkOnly() {
    return parse(buildTree(NGINX_003));
  }

  public static InspectContainerResponse ngixWithIpv6CustomBridgeNetwork() {
    return parse(buildTree(NGINX_004));
  }

  public static InspectContainerResponse ngixWithIpv6DefaultIp() {
    return parse(buildTree(NGINX_005));
  }

  public static InspectContainerResponse ngixWithIpv4DefaultBridgeAndIpv6CustomBridgeNetwork() {
    return parse(buildTree(NGINX_006));
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
