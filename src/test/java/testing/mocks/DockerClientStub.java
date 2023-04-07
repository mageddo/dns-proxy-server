package testing.mocks;

import com.github.dockerjava.api.DockerClientDelegate;
import com.github.dockerjava.api.command.ConnectToNetworkCmd;
import com.github.dockerjava.api.command.DockerCmdSyncExec;
import com.github.dockerjava.core.command.ConnectToNetworkCmdImpl;
import lombok.Getter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@Getter
public class DockerClientStub extends DockerClientDelegate {

  private final ConnectToNetworkCmd connectToNetworkCmd;
  private final DockerCmdSyncExec<?, Void> execution;

  public DockerClientStub() {
    this.execution = mock(DockerCmdSyncExec.class);
    this.connectToNetworkCmd = spy(new ConnectToNetworkCmdImpl((DockerCmdSyncExec<ConnectToNetworkCmd, Void>) this.execution));
  }

  @Override
  public ConnectToNetworkCmd connectToNetworkCmd() {
    return this.connectToNetworkCmd;
  }
}
