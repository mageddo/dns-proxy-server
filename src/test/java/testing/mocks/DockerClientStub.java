package testing.mocks;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.github.dockerjava.api.DockerClientDelegate;
import com.github.dockerjava.api.command.ConnectToNetworkCmd;
import com.github.dockerjava.api.command.DockerCmdSyncExec;
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.core.command.ConnectToNetworkCmdImpl;
import com.github.dockerjava.core.command.InspectContainerCmdImpl;
import com.github.dockerjava.core.command.ListContainersCmdImpl;

import lombok.Getter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@Getter
public class DockerClientStub extends DockerClientDelegate {

  private final ConnectToNetworkCmd connectToNetworkCmd;
  private final ListContainersCmd listContainersCmd;
  private final Map<String, InspectContainerCmd> inspectContainerCmdMap;

  private final DockerCmdSyncExec<ConnectToNetworkCmd, Void> connectToNetworkExecution;
  private final ListContainersCmd.Exec listContainersExecution;
  private final InspectContainerCmd.Exec inspectContainerExecution;

  public DockerClientStub() {
    this.connectToNetworkExecution = mock(DockerCmdSyncExec.class);
    this.listContainersExecution = mock(ListContainersCmd.Exec.class);
    this.inspectContainerExecution = mock(InspectContainerCmd.Exec.class);

    this.connectToNetworkCmd = spy(new ConnectToNetworkCmdImpl(this.connectToNetworkExecution));
    this.listContainersCmd = spy(new ListContainersCmdImpl(this.listContainersExecution));
    this.inspectContainerCmdMap = new HashMap<>();
  }

  @Override
  public ConnectToNetworkCmd connectToNetworkCmd() {
    return this.connectToNetworkCmd;
  }

  @Override
  public ListContainersCmd listContainersCmd() {
    return this.listContainersCmd;
  }

  @Override
  public InspectContainerCmd inspectContainerCmd(@Nonnull String containerId) {
    if (this.inspectContainerCmdMap.containsKey(containerId)) {
      return this.inspectContainerCmdMap.get(containerId);
    } else {
      final var inspectCmd = spy(
          new InspectContainerCmdImpl(this.inspectContainerExecution, containerId));
      inspectContainerCmdMap.put(containerId, inspectCmd);
      return inspectCmd;
    }
  }
}
