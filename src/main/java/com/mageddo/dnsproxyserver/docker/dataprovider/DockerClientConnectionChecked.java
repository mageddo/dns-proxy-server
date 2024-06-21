package com.mageddo.dnsproxyserver.docker.dataprovider;

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

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DockerClientConnectionChecked implements DockerClient {

  private final DockerClient delegate;

  public DockerClientConnectionChecked(DockerClient delegate){
    this.delegate = delegate;
  }

  public DockerClient getDelegate() {
    return delegate;
  }

  @Override
  public AuthConfig authConfig() throws DockerException {
    return this.getDelegate().authConfig();
  }

  @Override
  public AuthCmd authCmd() {
    return this.getDelegate().authCmd();
  }

  @Override
  public InfoCmd infoCmd() {
    return this.getDelegate().infoCmd();
  }

  @Override
  public PingCmd pingCmd() {
    return this.getDelegate().pingCmd();
  }

  @Override
  public VersionCmd versionCmd() {
    return this.getDelegate().versionCmd();
  }

  @Override
  public PullImageCmd pullImageCmd(@Nonnull String repository) {
    this.checkConnection();
    return this.getDelegate().pullImageCmd(repository);
  }

  private void checkConnection() {
    throw new UnsupportedOperationException();
  }

  @Override
  public PushImageCmd pushImageCmd(@Nonnull String name) {
    return this.getDelegate().pushImageCmd(name);
  }

  @Override
  public PushImageCmd pushImageCmd(@Nonnull Identifier identifier) {
    return this.getDelegate().pushImageCmd(identifier);
  }

  @Override
  public CreateImageCmd createImageCmd(@Nonnull String repository, @Nonnull InputStream imageStream) {
    return this.getDelegate().createImageCmd(repository, imageStream);
  }

  @Override
  public LoadImageCmd loadImageCmd(@Nonnull InputStream imageStream) {
    return this.getDelegate().loadImageCmd(imageStream);
  }

  @Override
  public LoadImageAsyncCmd loadImageAsyncCmd(@Nonnull InputStream imageStream) {
    return this.getDelegate().loadImageAsyncCmd(imageStream);
  }

  @Override
  public SearchImagesCmd searchImagesCmd(@Nonnull String term) {
    return this.getDelegate().searchImagesCmd(term);
  }

  @Override
  public RemoveImageCmd removeImageCmd(@Nonnull String imageId) {
    return this.getDelegate().removeImageCmd(imageId);
  }

  @Override
  public ListImagesCmd listImagesCmd() {
    return this.getDelegate().listImagesCmd();
  }

  @Override
  public InspectImageCmd inspectImageCmd(@Nonnull String imageId) {
    return this.getDelegate().inspectImageCmd(imageId);
  }

  @Override
  public SaveImageCmd saveImageCmd(@Nonnull String name) {
    return this.getDelegate().saveImageCmd(name);
  }

  @Override
  public SaveImagesCmd saveImagesCmd() {
    return this.getDelegate().saveImagesCmd();
  }

  @Override
  public ListContainersCmd listContainersCmd() {
    return this.getDelegate().listContainersCmd();
  }

  @Override
  public CreateContainerCmd createContainerCmd(@Nonnull String image) {
    return this.getDelegate().createContainerCmd(image);
  }

  @Override
  public StartContainerCmd startContainerCmd(@Nonnull String containerId) {
    return this.getDelegate().startContainerCmd(containerId);
  }

  @Override
  public ExecCreateCmd execCreateCmd(@Nonnull String containerId) {
    return this.getDelegate().execCreateCmd(containerId);
  }

  @Override
  public ResizeExecCmd resizeExecCmd(@Nonnull String execId) {
    return this.getDelegate().resizeExecCmd(execId);
  }

  @Override
  public InspectContainerCmd inspectContainerCmd(@Nonnull String containerId) {
    return this.getDelegate().inspectContainerCmd(containerId);
  }

  @Override
  public RemoveContainerCmd removeContainerCmd(@Nonnull String containerId) {
    return this.getDelegate().removeContainerCmd(containerId);
  }

  @Override
  public WaitContainerCmd waitContainerCmd(@Nonnull String containerId) {
    return this.getDelegate().waitContainerCmd(containerId);
  }

  @Override
  public AttachContainerCmd attachContainerCmd(@Nonnull String containerId) {
    return this.getDelegate().attachContainerCmd(containerId);
  }

  @Override
  public ExecStartCmd execStartCmd(@Nonnull String execId) {
    return this.getDelegate().execStartCmd(execId);
  }

  @Override
  public InspectExecCmd inspectExecCmd(@Nonnull String execId) {
    return this.getDelegate().inspectExecCmd(execId);
  }

  @Override
  public LogContainerCmd logContainerCmd(@Nonnull String containerId) {
    return this.getDelegate().logContainerCmd(containerId);
  }

  @Override
  public CopyArchiveFromContainerCmd copyArchiveFromContainerCmd(@Nonnull String containerId, @Nonnull String resource) {
    return this.getDelegate().copyArchiveFromContainerCmd(containerId, resource);
  }

  @Override
  public CopyFileFromContainerCmd copyFileFromContainerCmd(@Nonnull String containerId, @Nonnull String resource) {
    return this.getDelegate().copyFileFromContainerCmd(containerId, resource);
  }

  @Override
  public CopyArchiveToContainerCmd copyArchiveToContainerCmd(@Nonnull String containerId) {
    return this.getDelegate().copyArchiveToContainerCmd(containerId);
  }

  @Override
  public ContainerDiffCmd containerDiffCmd(@Nonnull String containerId) {
    return this.getDelegate().containerDiffCmd(containerId);
  }

  @Override
  public StopContainerCmd stopContainerCmd(@Nonnull String containerId) {
    return this.getDelegate().stopContainerCmd(containerId);
  }

  @Override
  public KillContainerCmd killContainerCmd(@Nonnull String containerId) {
    return this.getDelegate().killContainerCmd(containerId);
  }

  @Override
  public UpdateContainerCmd updateContainerCmd(@Nonnull String containerId) {
    return this.getDelegate().updateContainerCmd(containerId);
  }

  @Override
  public RenameContainerCmd renameContainerCmd(@Nonnull String containerId) {
    return this.getDelegate().renameContainerCmd(containerId);
  }

  @Override
  public RestartContainerCmd restartContainerCmd(@Nonnull String containerId) {
    return this.getDelegate().restartContainerCmd(containerId);
  }

  @Override
  public ResizeContainerCmd resizeContainerCmd(@Nonnull String containerId) {
    return this.getDelegate().resizeContainerCmd(containerId);
  }

  @Override
  public CommitCmd commitCmd(@Nonnull String containerId) {
    return this.getDelegate().commitCmd(containerId);
  }

  @Override
  public BuildImageCmd buildImageCmd() {
    return this.getDelegate().buildImageCmd();
  }

  @Override
  public BuildImageCmd buildImageCmd(File dockerFileOrFolder) {
    return this.getDelegate().buildImageCmd(dockerFileOrFolder);
  }

  @Override
  public BuildImageCmd buildImageCmd(InputStream tarInputStream) {
    return this.getDelegate().buildImageCmd(tarInputStream);
  }

  @Override
  public TopContainerCmd topContainerCmd(String containerId) {
    return this.getDelegate().topContainerCmd(containerId);
  }

  @Override
  public TagImageCmd tagImageCmd(String imageId, String imageNameWithRepository, String tag) {
    return this.getDelegate().tagImageCmd(imageId, imageNameWithRepository, tag);
  }

  @Override
  public PauseContainerCmd pauseContainerCmd(String containerId) {
    return this.getDelegate().pauseContainerCmd(containerId);
  }

  @Override
  public UnpauseContainerCmd unpauseContainerCmd(String containerId) {
    return this.getDelegate().unpauseContainerCmd(containerId);
  }

  @Override
  public EventsCmd eventsCmd() {
    return this.getDelegate().eventsCmd();
  }

  @Override
  public StatsCmd statsCmd(String containerId) {
    return this.getDelegate().statsCmd(containerId);
  }

  @Override
  public CreateVolumeCmd createVolumeCmd() {
    return this.getDelegate().createVolumeCmd();
  }

  @Override
  public InspectVolumeCmd inspectVolumeCmd(String name) {
    return this.getDelegate().inspectVolumeCmd(name);
  }

  @Override
  public RemoveVolumeCmd removeVolumeCmd(String name) {
    return this.getDelegate().removeVolumeCmd(name);
  }

  @Override
  public ListVolumesCmd listVolumesCmd() {
    return this.getDelegate().listVolumesCmd();
  }

  @Override
  public ListNetworksCmd listNetworksCmd() {
    return this.getDelegate().listNetworksCmd();
  }

  @Override
  public InspectNetworkCmd inspectNetworkCmd() {
    return this.getDelegate().inspectNetworkCmd();
  }

  @Override
  public CreateNetworkCmd createNetworkCmd() {
    return this.getDelegate().createNetworkCmd();
  }

  @Override
  public RemoveNetworkCmd removeNetworkCmd(@Nonnull String networkId) {
    return this.getDelegate().removeNetworkCmd(networkId);
  }

  @Override
  public ConnectToNetworkCmd connectToNetworkCmd() {
    return this.getDelegate().connectToNetworkCmd();
  }

  @Override
  public DisconnectFromNetworkCmd disconnectFromNetworkCmd() {
    return this.getDelegate().disconnectFromNetworkCmd();
  }

  @Override
  public InitializeSwarmCmd initializeSwarmCmd(SwarmSpec swarmSpec) {
    return this.getDelegate().initializeSwarmCmd(swarmSpec);
  }

  @Override
  public InspectSwarmCmd inspectSwarmCmd() {
    return this.getDelegate().inspectSwarmCmd();
  }

  @Override
  public JoinSwarmCmd joinSwarmCmd() {
    return this.getDelegate().joinSwarmCmd();
  }

  @Override
  public LeaveSwarmCmd leaveSwarmCmd() {
    return this.getDelegate().leaveSwarmCmd();
  }

  @Override
  public UpdateSwarmCmd updateSwarmCmd(SwarmSpec swarmSpec) {
    return this.getDelegate().updateSwarmCmd(swarmSpec);
  }

  @Override
  public UpdateSwarmNodeCmd updateSwarmNodeCmd() {
    return this.getDelegate().updateSwarmNodeCmd();
  }

  @Override
  public RemoveSwarmNodeCmd removeSwarmNodeCmd(String swarmNodeId) {
    return this.getDelegate().removeSwarmNodeCmd(swarmNodeId);
  }

  @Override
  public ListSwarmNodesCmd listSwarmNodesCmd() {
    return this.getDelegate().listSwarmNodesCmd();
  }

  @Override
  public ListServicesCmd listServicesCmd() {
    return this.getDelegate().listServicesCmd();
  }

  @Override
  public CreateServiceCmd createServiceCmd(ServiceSpec serviceSpec) {
    return this.getDelegate().createServiceCmd(serviceSpec);
  }

  @Override
  public InspectServiceCmd inspectServiceCmd(String serviceId) {
    return this.getDelegate().inspectServiceCmd(serviceId);
  }

  @Override
  public UpdateServiceCmd updateServiceCmd(String serviceId, ServiceSpec serviceSpec) {
    return this.getDelegate().updateServiceCmd(serviceId, serviceSpec);
  }

  @Override
  public RemoveServiceCmd removeServiceCmd(String serviceId) {
    return this.getDelegate().removeServiceCmd(serviceId);
  }

  @Override
  public ListTasksCmd listTasksCmd() {
    return this.getDelegate().listTasksCmd();
  }

  @Override
  public LogSwarmObjectCmd logServiceCmd(String serviceId) {
    return this.getDelegate().logServiceCmd(serviceId);
  }

  @Override
  public LogSwarmObjectCmd logTaskCmd(String taskId) {
    return this.getDelegate().logTaskCmd(taskId);
  }

  @Override
  public PruneCmd pruneCmd(PruneType pruneType) {
    return this.getDelegate().pruneCmd(pruneType);
  }

  @Override
  public ListSecretsCmd listSecretsCmd() {
    return this.getDelegate().listSecretsCmd();
  }

  @Override
  public CreateSecretCmd createSecretCmd(SecretSpec secretSpec) {
    return this.getDelegate().createSecretCmd(secretSpec);
  }

  @Override
  public RemoveSecretCmd removeSecretCmd(String secretId) {
    return this.getDelegate().removeSecretCmd(secretId);
  }

  @Override
  public ListConfigsCmd listConfigsCmd() {
    return this.getDelegate().listConfigsCmd();
  }

  @Override
  public CreateConfigCmd createConfigCmd() {
    return this.getDelegate().createConfigCmd();
  }

  @Override
  public InspectConfigCmd inspectConfigCmd(String configId) {
    return this.getDelegate().inspectConfigCmd(configId);
  }

  @Override
  public RemoveConfigCmd removeConfigCmd(String configId) {
    return this.getDelegate().removeConfigCmd(configId);
  }

  @Override
  public void close() throws IOException {
    this.getDelegate().close();
  }
}
