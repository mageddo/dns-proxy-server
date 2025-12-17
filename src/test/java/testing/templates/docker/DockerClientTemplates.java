package testing.templates.docker;

import org.mockito.Mockito;

import testing.mocks.DockerClientStub;

public class DockerClientTemplates {
  public static DockerClientStub buildSpy() {
    return Mockito.spy(new DockerClientStub());
  }
}
