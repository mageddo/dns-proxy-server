package testing.templates.docker;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.dockerjava.api.model.Network;
import com.mageddo.dnsproxyserver.docker.domain.Drivers;
import com.mageddo.json.JsonUtils;
import lombok.SneakyThrows;

import static com.mageddo.utils.TestUtils.readString;

public class NetworkTemplates {

  @SneakyThrows
  public static Network withBridgeDriver(String name) {
    final var node = JsonNodeFactory.instance.objectNode()
      .put("Name", name)
      .put("Driver", Drivers.BRIDGE);
    return JsonUtils
      .instance()
      .treeToValue(node, Network.class)
      ;
  }

  @SneakyThrows
  public static Network withOverlayDriver(String name) {
    final var node = JsonNodeFactory.instance.objectNode()
      .put("Name", name)
      .put("Driver", Drivers.OVERLAY);
    return JsonUtils
      .instance()
      .treeToValue(node, Network.class)
      ;
  }

  public static Network buildBridgeIpv4AndIpv6Network() {
    return JsonUtils.readValue(readString("/templates/docker/network/001.json"), Network.class);
  }

  public static Network buildBridgeIpv4OnlyNetwork() {
    return JsonUtils.readValue(readString("/templates/docker/network/002.json"), Network.class);
  }

  public static Network buildHostNetworkWithNoIpam() {
    return JsonUtils.readValue(readString("/templates/docker/network/003.json"), Network.class);
  }

}
