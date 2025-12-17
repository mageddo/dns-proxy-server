package com.mageddo.dnsproxyserver.docker.dataprovider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.AttachContainerCmd;
import com.github.dockerjava.api.command.AuthCmd;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.CommitCmd;
import com.github.dockerjava.api.command.ConnectToNetworkCmd;
import com.github.dockerjava.api.command.ContainerDiffCmd;
import com.github.dockerjava.api.command.CopyArchiveFromContainerCmd;
import com.github.dockerjava.api.command.CopyArchiveToContainerCmd;
import com.github.dockerjava.api.command.CopyFileFromContainerCmd;
import com.github.dockerjava.api.command.CreateConfigCmd;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateImageCmd;
import com.github.dockerjava.api.command.CreateNetworkCmd;
import com.github.dockerjava.api.command.CreateSecretCmd;
import com.github.dockerjava.api.command.CreateServiceCmd;
import com.github.dockerjava.api.command.CreateVolumeCmd;
import com.github.dockerjava.api.command.DisconnectFromNetworkCmd;
import com.github.dockerjava.api.command.EventsCmd;
import com.github.dockerjava.api.command.ExecCreateCmd;
import com.github.dockerjava.api.command.ExecStartCmd;
import com.github.dockerjava.api.command.InfoCmd;
import com.github.dockerjava.api.command.InitializeSwarmCmd;
import com.github.dockerjava.api.command.InspectConfigCmd;
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.InspectExecCmd;
import com.github.dockerjava.api.command.InspectImageCmd;
import com.github.dockerjava.api.command.InspectNetworkCmd;
import com.github.dockerjava.api.command.InspectServiceCmd;
import com.github.dockerjava.api.command.InspectSwarmCmd;
import com.github.dockerjava.api.command.InspectVolumeCmd;
import com.github.dockerjava.api.command.JoinSwarmCmd;
import com.github.dockerjava.api.command.KillContainerCmd;
import com.github.dockerjava.api.command.LeaveSwarmCmd;
import com.github.dockerjava.api.command.ListConfigsCmd;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.command.ListNetworksCmd;
import com.github.dockerjava.api.command.ListSecretsCmd;
import com.github.dockerjava.api.command.ListServicesCmd;
import com.github.dockerjava.api.command.ListSwarmNodesCmd;
import com.github.dockerjava.api.command.ListTasksCmd;
import com.github.dockerjava.api.command.ListVolumesCmd;
import com.github.dockerjava.api.command.LoadImageAsyncCmd;
import com.github.dockerjava.api.command.LoadImageCmd;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.command.LogSwarmObjectCmd;
import com.github.dockerjava.api.command.PauseContainerCmd;
import com.github.dockerjava.api.command.PingCmd;
import com.github.dockerjava.api.command.PruneCmd;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PushImageCmd;
import com.github.dockerjava.api.command.RemoveConfigCmd;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.command.RemoveImageCmd;
import com.github.dockerjava.api.command.RemoveNetworkCmd;
import com.github.dockerjava.api.command.RemoveSecretCmd;
import com.github.dockerjava.api.command.RemoveServiceCmd;
import com.github.dockerjava.api.command.RemoveSwarmNodeCmd;
import com.github.dockerjava.api.command.RemoveVolumeCmd;
import com.github.dockerjava.api.command.RenameContainerCmd;
import com.github.dockerjava.api.command.ResizeContainerCmd;
import com.github.dockerjava.api.command.ResizeExecCmd;
import com.github.dockerjava.api.command.RestartContainerCmd;
import com.github.dockerjava.api.command.SaveImageCmd;
import com.github.dockerjava.api.command.SaveImagesCmd;
import com.github.dockerjava.api.command.SearchImagesCmd;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.command.StopContainerCmd;
import com.github.dockerjava.api.command.TagImageCmd;
import com.github.dockerjava.api.command.TopContainerCmd;
import com.github.dockerjava.api.command.UnpauseContainerCmd;
import com.github.dockerjava.api.command.UpdateContainerCmd;
import com.github.dockerjava.api.command.UpdateServiceCmd;
import com.github.dockerjava.api.command.UpdateSwarmCmd;
import com.github.dockerjava.api.command.UpdateSwarmNodeCmd;
import com.github.dockerjava.api.command.VersionCmd;
import com.github.dockerjava.api.command.WaitContainerCmd;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.Identifier;
import com.github.dockerjava.api.model.PruneType;
import com.github.dockerjava.api.model.SecretSpec;
import com.github.dockerjava.api.model.ServiceSpec;
import com.github.dockerjava.api.model.SwarmSpec;
import com.mageddo.dnsproxyserver.docker.application.DockerConnectionCheck;

public class DockerClientConnectionChecked implements DockerClient {

  private final DockerClient delegate;
  private final DockerConnectionCheck dockerConnectionCheck;

  public DockerClientConnectionChecked(DockerClient delegate) {
    this.delegate = delegate;
    this.dockerConnectionCheck = new DockerConnectionCheck(delegate);
  }

  DockerClient getDelegate() {
    return this.delegate;
  }

  void checkConnection() {
    if (!this.dockerConnectionCheck.isConnected()) {
      throw new IllegalStateException("Can't connect to docker API");
    }
  }

  @Override
  public AuthConfig authConfig() throws DockerException {
    return this.getDelegate()
        .authConfig();
  }

  @Override
  public AuthCmd authCmd() {
    return this.getDelegate()
        .authCmd();
  }

  @Override
  public InfoCmd infoCmd() {
    return this.getDelegate()
        .infoCmd();
  }

  @Override
  public PingCmd pingCmd() {
    return this.getDelegate()
        .pingCmd();
  }

  @Override
  public VersionCmd versionCmd() {
    return this.getDelegate()
        .versionCmd();
  }

  @Override
  public PullImageCmd pullImageCmd(@Nonnull String repository) {
    this.checkConnection();
    return this.getDelegate()
        .pullImageCmd(repository);
  }

  @Override
  public PushImageCmd pushImageCmd(@Nonnull String name) {
    this.checkConnection();
    return this.getDelegate()
        .pushImageCmd(name);
  }

  @Override
  public PushImageCmd pushImageCmd(@Nonnull Identifier identifier) {
    this.checkConnection();
    return this.getDelegate()
        .pushImageCmd(identifier);
  }

  @Override
  public CreateImageCmd createImageCmd(
      @Nonnull String repository, @Nonnull InputStream imageStream) {
    this.checkConnection();
    return this.getDelegate()
        .createImageCmd(repository, imageStream);
  }

  @Override
  public LoadImageCmd loadImageCmd(@Nonnull InputStream imageStream) {
    this.checkConnection();
    return this.getDelegate()
        .loadImageCmd(imageStream);
  }

  @Override
  public LoadImageAsyncCmd loadImageAsyncCmd(@Nonnull InputStream imageStream) {
    this.checkConnection();
    return this.getDelegate()
        .loadImageAsyncCmd(imageStream);
  }

  @Override
  public SearchImagesCmd searchImagesCmd(@Nonnull String term) {
    this.checkConnection();
    return this.getDelegate()
        .searchImagesCmd(term);
  }

  @Override
  public RemoveImageCmd removeImageCmd(@Nonnull String imageId) {
    this.checkConnection();
    return this.getDelegate()
        .removeImageCmd(imageId);
  }

  @Override
  public ListImagesCmd listImagesCmd() {
    this.checkConnection();
    return this.getDelegate()
        .listImagesCmd();
  }

  @Override
  public InspectImageCmd inspectImageCmd(@Nonnull String imageId) {
    this.checkConnection();
    return this.getDelegate()
        .inspectImageCmd(imageId);
  }

  @Override
  public SaveImageCmd saveImageCmd(@Nonnull String name) {
    this.checkConnection();
    return this.getDelegate()
        .saveImageCmd(name);
  }

  @Override
  public SaveImagesCmd saveImagesCmd() {
    this.checkConnection();
    return this.getDelegate()
        .saveImagesCmd();
  }

  @Override
  public ListContainersCmd listContainersCmd() {
    this.checkConnection();
    return this.getDelegate()
        .listContainersCmd();
  }

  @Override
  public CreateContainerCmd createContainerCmd(@Nonnull String image) {
    this.checkConnection();
    return this.getDelegate()
        .createContainerCmd(image);
  }

  @Override
  public StartContainerCmd startContainerCmd(@Nonnull String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .startContainerCmd(containerId);
  }

  @Override
  public ExecCreateCmd execCreateCmd(@Nonnull String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .execCreateCmd(containerId);
  }

  @Override
  public ResizeExecCmd resizeExecCmd(@Nonnull String execId) {
    this.checkConnection();
    return this.getDelegate()
        .resizeExecCmd(execId);
  }

  @Override
  public InspectContainerCmd inspectContainerCmd(@Nonnull String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .inspectContainerCmd(containerId);
  }

  @Override
  public RemoveContainerCmd removeContainerCmd(@Nonnull String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .removeContainerCmd(containerId);
  }

  @Override
  public WaitContainerCmd waitContainerCmd(@Nonnull String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .waitContainerCmd(containerId);
  }

  @Override
  public AttachContainerCmd attachContainerCmd(@Nonnull String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .attachContainerCmd(containerId);
  }

  @Override
  public ExecStartCmd execStartCmd(@Nonnull String execId) {
    this.checkConnection();
    return this.getDelegate()
        .execStartCmd(execId);
  }

  @Override
  public InspectExecCmd inspectExecCmd(@Nonnull String execId) {
    this.checkConnection();
    return this.getDelegate()
        .inspectExecCmd(execId);
  }

  @Override
  public LogContainerCmd logContainerCmd(@Nonnull String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .logContainerCmd(containerId);
  }

  @Override
  public CopyArchiveFromContainerCmd copyArchiveFromContainerCmd(
      @Nonnull String containerId, @Nonnull String resource) {
    this.checkConnection();
    return this.getDelegate()
        .copyArchiveFromContainerCmd(containerId, resource);
  }

  @Override
  public CopyFileFromContainerCmd copyFileFromContainerCmd(
      @Nonnull String containerId, @Nonnull String resource) {
    this.checkConnection();
    return this.getDelegate()
        .copyFileFromContainerCmd(containerId, resource);
  }

  @Override
  public CopyArchiveToContainerCmd copyArchiveToContainerCmd(@Nonnull String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .copyArchiveToContainerCmd(containerId);
  }

  @Override
  public ContainerDiffCmd containerDiffCmd(@Nonnull String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .containerDiffCmd(containerId);
  }

  @Override
  public StopContainerCmd stopContainerCmd(@Nonnull String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .stopContainerCmd(containerId);
  }

  @Override
  public KillContainerCmd killContainerCmd(@Nonnull String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .killContainerCmd(containerId);
  }

  @Override
  public UpdateContainerCmd updateContainerCmd(@Nonnull String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .updateContainerCmd(containerId);
  }

  @Override
  public RenameContainerCmd renameContainerCmd(@Nonnull String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .renameContainerCmd(containerId);
  }

  @Override
  public RestartContainerCmd restartContainerCmd(@Nonnull String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .restartContainerCmd(containerId);
  }

  @Override
  public ResizeContainerCmd resizeContainerCmd(@Nonnull String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .resizeContainerCmd(containerId);
  }

  @Override
  public CommitCmd commitCmd(@Nonnull String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .commitCmd(containerId);
  }

  @Override
  public BuildImageCmd buildImageCmd() {
    this.checkConnection();
    return this.getDelegate()
        .buildImageCmd();
  }

  @Override
  public BuildImageCmd buildImageCmd(File dockerFileOrFolder) {
    this.checkConnection();
    return this.getDelegate()
        .buildImageCmd(dockerFileOrFolder);
  }

  @Override
  public BuildImageCmd buildImageCmd(InputStream tarInputStream) {
    this.checkConnection();
    return this.getDelegate()
        .buildImageCmd(tarInputStream);
  }

  @Override
  public TopContainerCmd topContainerCmd(String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .topContainerCmd(containerId);
  }

  @Override
  public TagImageCmd tagImageCmd(String imageId, String imageNameWithRepository, String tag) {
    this.checkConnection();
    return this.getDelegate()
        .tagImageCmd(imageId, imageNameWithRepository, tag);
  }

  @Override
  public PauseContainerCmd pauseContainerCmd(String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .pauseContainerCmd(containerId);
  }

  @Override
  public UnpauseContainerCmd unpauseContainerCmd(String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .unpauseContainerCmd(containerId);
  }

  @Override
  public EventsCmd eventsCmd() {
    this.checkConnection();
    return this.getDelegate()
        .eventsCmd();
  }

  @Override
  public StatsCmd statsCmd(String containerId) {
    this.checkConnection();
    return this.getDelegate()
        .statsCmd(containerId);
  }

  @Override
  public CreateVolumeCmd createVolumeCmd() {
    this.checkConnection();
    return this.getDelegate()
        .createVolumeCmd();
  }

  @Override
  public InspectVolumeCmd inspectVolumeCmd(String name) {
    this.checkConnection();
    return this.getDelegate()
        .inspectVolumeCmd(name);
  }

  @Override
  public RemoveVolumeCmd removeVolumeCmd(String name) {
    this.checkConnection();
    return this.getDelegate()
        .removeVolumeCmd(name);
  }

  @Override
  public ListVolumesCmd listVolumesCmd() {
    this.checkConnection();
    return this.getDelegate()
        .listVolumesCmd();
  }

  @Override
  public ListNetworksCmd listNetworksCmd() {
    this.checkConnection();
    return this.getDelegate()
        .listNetworksCmd();
  }

  @Override
  public InspectNetworkCmd inspectNetworkCmd() {
    this.checkConnection();
    return this.getDelegate()
        .inspectNetworkCmd();
  }

  @Override
  public CreateNetworkCmd createNetworkCmd() {
    this.checkConnection();
    return this.getDelegate()
        .createNetworkCmd();
  }

  @Override
  public RemoveNetworkCmd removeNetworkCmd(@Nonnull String networkId) {
    this.checkConnection();
    return this.getDelegate()
        .removeNetworkCmd(networkId);
  }

  @Override
  public ConnectToNetworkCmd connectToNetworkCmd() {
    this.checkConnection();
    return this.getDelegate()
        .connectToNetworkCmd();
  }

  @Override
  public DisconnectFromNetworkCmd disconnectFromNetworkCmd() {
    this.checkConnection();
    return this.getDelegate()
        .disconnectFromNetworkCmd();
  }

  @Override
  public InitializeSwarmCmd initializeSwarmCmd(SwarmSpec swarmSpec) {
    this.checkConnection();
    return this.getDelegate()
        .initializeSwarmCmd(swarmSpec);
  }

  @Override
  public InspectSwarmCmd inspectSwarmCmd() {
    this.checkConnection();
    return this.getDelegate()
        .inspectSwarmCmd();
  }

  @Override
  public JoinSwarmCmd joinSwarmCmd() {
    this.checkConnection();
    return this.getDelegate()
        .joinSwarmCmd();
  }

  @Override
  public LeaveSwarmCmd leaveSwarmCmd() {
    this.checkConnection();
    return this.getDelegate()
        .leaveSwarmCmd();
  }

  @Override
  public UpdateSwarmCmd updateSwarmCmd(SwarmSpec swarmSpec) {
    this.checkConnection();
    return this.getDelegate()
        .updateSwarmCmd(swarmSpec);
  }

  @Override
  public UpdateSwarmNodeCmd updateSwarmNodeCmd() {
    this.checkConnection();
    return this.getDelegate()
        .updateSwarmNodeCmd();
  }

  @Override
  public RemoveSwarmNodeCmd removeSwarmNodeCmd(String swarmNodeId) {
    this.checkConnection();
    return this.getDelegate()
        .removeSwarmNodeCmd(swarmNodeId);
  }

  @Override
  public ListSwarmNodesCmd listSwarmNodesCmd() {
    this.checkConnection();
    return this.getDelegate()
        .listSwarmNodesCmd();
  }

  @Override
  public ListServicesCmd listServicesCmd() {
    this.checkConnection();
    return this.getDelegate()
        .listServicesCmd();
  }

  @Override
  public CreateServiceCmd createServiceCmd(ServiceSpec serviceSpec) {
    this.checkConnection();
    return this.getDelegate()
        .createServiceCmd(serviceSpec);
  }

  @Override
  public InspectServiceCmd inspectServiceCmd(String serviceId) {
    this.checkConnection();
    return this.getDelegate()
        .inspectServiceCmd(serviceId);
  }

  @Override
  public UpdateServiceCmd updateServiceCmd(String serviceId, ServiceSpec serviceSpec) {
    this.checkConnection();
    return this.getDelegate()
        .updateServiceCmd(serviceId, serviceSpec);
  }

  @Override
  public RemoveServiceCmd removeServiceCmd(String serviceId) {
    this.checkConnection();
    return this.getDelegate()
        .removeServiceCmd(serviceId);
  }

  @Override
  public ListTasksCmd listTasksCmd() {
    this.checkConnection();
    return this.getDelegate()
        .listTasksCmd();
  }

  @Override
  public LogSwarmObjectCmd logServiceCmd(String serviceId) {
    this.checkConnection();
    return this.getDelegate()
        .logServiceCmd(serviceId);
  }

  @Override
  public LogSwarmObjectCmd logTaskCmd(String taskId) {
    this.checkConnection();
    return this.getDelegate()
        .logTaskCmd(taskId);
  }

  @Override
  public PruneCmd pruneCmd(PruneType pruneType) {
    this.checkConnection();
    return this.getDelegate()
        .pruneCmd(pruneType);
  }

  @Override
  public ListSecretsCmd listSecretsCmd() {
    this.checkConnection();
    return this.getDelegate()
        .listSecretsCmd();
  }

  @Override
  public CreateSecretCmd createSecretCmd(SecretSpec secretSpec) {
    this.checkConnection();
    return this.getDelegate()
        .createSecretCmd(secretSpec);
  }

  @Override
  public RemoveSecretCmd removeSecretCmd(String secretId) {
    this.checkConnection();
    return this.getDelegate()
        .removeSecretCmd(secretId);
  }

  @Override
  public ListConfigsCmd listConfigsCmd() {
    this.checkConnection();
    return this.getDelegate()
        .listConfigsCmd();
  }

  @Override
  public CreateConfigCmd createConfigCmd() {
    this.checkConnection();
    return this.getDelegate()
        .createConfigCmd();
  }

  @Override
  public InspectConfigCmd inspectConfigCmd(String configId) {
    this.checkConnection();
    return this.getDelegate()
        .inspectConfigCmd(configId);
  }

  @Override
  public RemoveConfigCmd removeConfigCmd(String configId) {
    this.checkConnection();
    return this.getDelegate()
        .removeConfigCmd(configId);
  }

  @Override
  public void close() throws IOException {
    this.getDelegate()
        .close();
  }
}
