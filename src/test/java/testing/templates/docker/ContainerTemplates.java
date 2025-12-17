package testing.templates.docker;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dockerjava.api.model.Container;
import com.mageddo.json.JsonUtils;

import lombok.SneakyThrows;

import static testing.templates.docker.InspectContainerResponseTemplates.buildTree;

public class ContainerTemplates {

  static final String DPS_CONTAINER = "/templates/docker/container-list/001.json";
  static final String COFFEE_MAKER_CHECKOUT = "/templates/docker/container-list/002.json";

  public static Container buildDpsContainer() {
    final var tree = buildTree(DPS_CONTAINER);
    return parse(tree);
  }

  public static Container buildRegularContainerCoffeeMakerCheckout() {
    final var tree = buildTree(COFFEE_MAKER_CHECKOUT);
    return parse(tree);
  }

  @SneakyThrows
  private static Container parse(ObjectNode tree) {
    return JsonUtils
        .instance()
        .treeToValue(tree, Container.class);
  }
}
