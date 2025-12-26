---
title: Configuration Reference
weight: 3
pre: "<b>3. </b>"
---

Current Version: `3`. See [how to set the configurations][5].

### Server

| Name          | Description             | Default Value |
|---------------|-------------------------|---------------|
| `server.host` | Host to bind the ports. | `0.0.0.0`     |

---

### DNS Server

| Name                               | Description                                                        | Default Value |
|------------------------------------|--------------------------------------------------------------------|---------------|
| `server.dns.protocol`              | Protocol to start the DNS server.                                  | `UDP_TCP`     |
| `server.dns.port`                  | Port where the DNS server listens.                                 | `53`          |
| `server.dns.noEntriesResponseCode` | Response code returned when no entries are resolved by any solver. | `3`           |

---

### DoH Server

| Name              | Description                                     | Default Value |
|-------------------|-------------------------------------------------|---------------|
| `server.doh.port` | When set, will activate doh server on that port | ``            |

---

### WEB Server

| Name              | Description   | Default Value |
|-------------------|---------------|---------------|
| `server.web.port` | Web GUI port. | `5380`        |

---

### Solvers

Common DNS resolution mechanisms used by DPS. Solvers are evaluated according to their activation and configuration.

---

### Remote Solver

| Name                       | Description                                                                            | Default Value |
|----------------------------|----------------------------------------------------------------------------------------|---------------|
| `solver.remote.active`     | Enables or disables querying remote DNS servers.                                       | `true`        |
| `solver.remote.dnsServers` | Remote DNS servers to be queried when resolution cannot be done locally or via Docker. | `[8.8.8.8]`   |

---

### Docker Solver

| Name                                   | Description                                                                             | Default Value |
|----------------------------------------|-----------------------------------------------------------------------------------------|---------------|
| `solver.docker.registerContainerNames` | Whether container or service names should be registered as DNS hostnames.               | `false`       |
| `solver.docker.domain`                 | Domain suffix used when registering Docker containers and services.                     | `docker`      |
| `solver.docker.hostMachineFallback`    | Whether the host machine IP should be returned when a container is found but has no IP. | `true`        |
| `solver.docker.dockerDaemonUri`        | Docker daemon URI used to connect to Docker.                                            | OS dependent  |

#### DPS Network

| Name                                         | Description                                                         | Default Value   |
|----------------------------------------------|---------------------------------------------------------------------|-----------------|
| `solver.docker.dpsNetwork.autoCreate`        | Whether DPS should automatically create a Docker bridge network.    | `false`         |
| `solver.docker.dpsNetwork.autoConnect`       | Whether all containers should be auto-connected to the DPS network. | `false`         |
| `solver.docker.dpsNetwork.configs`           | Docker network IP configuration                                     |                 |
| `solver.docker.dpsNetwork.configs[].subNet`  | Subnet                                                              | `172.20.0.0/16` |
| `solver.docker.dpsNetwork.configs[].ipRange` | Ip Range                                                            | `172.20.5.0/24` |
| `solver.docker.dpsNetwork.configs[].gateway` | Gateway                                                             | `172.20.5.1`    |

Default DPS network settings

```yaml
---
- subNet: 172.20.0.0/16
  ipRange: 172.20.5.0/24
  gateway: 172.20.5.1
- subNet: fc00:5c6f:db50::/64
  gateway: fc00:5c6f:db50::1
```

#### Network Priority when Solving Container IP

| Name                                               | Description                                                      | Default Value |
|----------------------------------------------------|------------------------------------------------------------------|---------------|
| `solver.docker.networks.preferred.names`           | Which networks DPS must prioritize when discovering container IP |               |
| `solver.docker.networks.preferred.overrideDefault` | If will disable DPS and BRIDGE default networks when solving     | false         |

See more on [specify from which network solve container][6].

### System Solver

| Name                                | Description                                    | Default Value |
|-------------------------------------|------------------------------------------------|---------------|
| `solver.system.hostMachineHostname` | Hostname that resolves to the host machine IP. | `host.docker` |

---

### Local Solver

| Name                     | Description                                           | Default Value     |
|--------------------------|-------------------------------------------------------|-------------------|
| `solver.local.activeEnv` | Active environment used to resolve local DNS entries. | `` (empty string) |

---

### Stub Solver

| Name                     | Description                                          | Default Value |
|--------------------------|------------------------------------------------------|---------------|
| `solver.stub.domainName` | Domain name used for stub solver resolved hostnames. | `stub`        |

---

### Default DNS

| Name                                        | Description                                                                         | Default Value                                                                                     |
|---------------------------------------------|-------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------|
| `defaultDns.active`                         | Whether DPS should be automatically configured as the system default DNS.           | `false`                                                                                           |
| `defaultDns.resolvConf.overrideNameServers` | Whether existing nameservers should be commented out or DPS should be placed first. | `true`                                                                                            |
| `defaultDns.resolvConf.paths`               | Resolv.conf or systemd-resolved configuration paths used to apply default DNS.      | /host/etc/systemd/resolved.conf,/host/etc/resolv.conf,/etc/systemd/resolved.conf,/etc/resolv.conf |

---

### Logs

| Name        | Description                                    | Default Value |
|-------------|------------------------------------------------|---------------|
| `log.level` | Logging level used by DPS.                     | `INFO`        |
| `log.file`  | Output target for logs (file path or console). | `console`     |

### Memory Limits

When on binary mode DPS has a default limit of `MaxHeapSize=50m` + `MaxNewSize=10m`, you can change that by:

**Command Line**
```bash
./dns-proxy-server -XX:MaxHeapSize=50m -XX:MaxNewSize=10m
```

**Docker Compose**
```yaml
services:
  dps:
    image: defreitas/dns-proxy-server
    command: -XX:MaxHeapSize=50m -XX:MaxNewSize=10m
```


### File Configuration Example

* [Solver remote circuit breaker configuration][3]

```yaml
version: 3
server:
  host: "0.0.0.0"
  dns:
    port: 53
    noEntriesResponseCode: 3
    protocol: UDP_TCP
  web:
    port: 5380
solver:
  remote:
    active: true
    dnsServers:
      - 8.8.8.8
      - 4.4.4.4:53
    circuitBreaker:
      type: CANARY_RATE_THRESHOLD
      failureRateThreshold: 21
      minimumNumberOfCalls: 50
      permittedNumberOfCallsInHalfOpenState: 10
  docker:
    registerContainerNames: false
    domain: docker
    hostMachineFallback: true
    dpsNetwork:
      name: dps
      autoCreate: false
      autoConnect: false
      configs:
        - subNet: 172.20.0.0/16
          ipRange: 172.20.5.0/24
          gateway: 172.20.5.1
        - subNet: fc00:5c6f:db50::/64
          gateway: fc00:5c6f:db50::1
    dockerDaemonUri:
    networks:
      preferred:
        names:
          - my-awesome-network
  system:
    hostMachineHostname: host.docker
  local:
    activeEnv: ''
    envs:
      - name: ''
        hostnames:
          - type: A
            hostname: dps-sample.dev
            ip: 192.168.0.254
            ttl: 30
  stub:
    domainName: stub
defaultDns:
  active: true
  resolvConf:
    paths: "/host/etc/systemd/resolved.conf,/host/etc/resolv.conf,/etc/systemd/resolved.conf,/etc/resolv.conf"
    overrideNameServers: true
log:
  level: DEBUG
  file: console
```

### Legacy Configuration

[[ref][4]]

[1]: {{%relref "2-features/auto-configuration-as-default-dns/_index.md" %}}
[2]: {{%relref "2-features/local-entries/_index.md" %}}
[3]: {{%relref "2-features/remote-solver-circuitbreaker/_index.en.md" %}}
[4]: {{%relref "3-configuration/legacy.en.md" %}}
[5]: {{%relref "3-configuration/format.en.md" %}}
[6]: {{%relref "2-features/specify-from-which-network-solve-container/_index.en.md" %}}
