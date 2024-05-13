package testing.templates.docker;

import com.github.dockerjava.api.model.Container;
import com.mageddo.json.JsonUtils;
import lombok.SneakyThrows;

import static testing.templates.docker.InspectContainerResponseTemplates.buildTree;

public class ContainerTemplates {

  static final String DPS_CONTAINER = "/templates/docker/container-list/001.json";

  @SneakyThrows
  public static Container buildDpsContainer() {
    final var tree = buildTree(DPS_CONTAINER);
    return JsonUtils
      .instance()
      .treeToValue(tree, Container.class);
  }
}
