---
title: Remote Solver Circuit Breaker
---

DPS use circuit breaker strategies to choose the most available Remote Server from the configured ones.

## Static Threshold

* Consider all remote servers circuits as **closed** on app start.
* Opens and closes circuits based on fixed number of failures or successes.


#### Configuration Example

```json
{
  "version": 2,
  "remoteDnsServers": [],
  "envs": [],
  "activeEnv": "",
  "webServerPort": 5380,
  "dnsServerPort": 53,
  "logLevel": "INFO",
  "logFile": "console",
  "registerContainerNames": false,
  "domain": "docker",
  "dpsNetwork": false,
  "dpsNetworkAutoConnect": false,
  "defaultDns": true,
  "hostMachineHostname": "host.docker",
  "serverProtocol": "UDP_TCP",
  "dockerHost": null,
  "resolvConfOverrideNameServers": true,
  "noRemoteServers": false,
  "noEntriesResponseCode": 3,
  "dockerSolverHostMachineFallbackActive": true,
  "solverRemote": {
    "circuitBreaker": {
      "failureThreshold": 3,
      "failureThresholdCapacity": 10,
      "successThreshold": 5,
      "testDelay": "PT20S"
    }
  }
}
```

* **failureThreshold**: How many attempts before open the circuit?
* **failureThresholdCapacity**: How many attempts store to the stack?
* **successThreshold**: How many attempts before close the circuit?
* **testDelay**: How much time to wait before test the circuit again?, see [Duration docs][1] for format explanation


## Canary Rate Threshold

* Consider all remote servers circuits as **open** on app start
* Opens and closes circuits based on percentage of failure

#### Consider all remote servers circuits as open on app start

Test them on startup and add the healthy ones as HALF_OPEN this will evict to app get resolution fails right on the
start because the first server on the remote servers list is offline.

#### Configuration Example

```json
{
  "version": 2,
  "remoteDnsServers": [],
  "envs": [],
  "activeEnv": "",
  "webServerPort": 5380,
  "dnsServerPort": 53,
  "logLevel": "INFO",
  "logFile": "console",
  "registerContainerNames": false,
  "domain": "docker",
  "dpsNetwork": false,
  "dpsNetworkAutoConnect": false,
  "defaultDns": true,
  "hostMachineHostname": "host.docker",
  "serverProtocol": "UDP_TCP",
  "dockerHost": null,
  "resolvConfOverrideNameServers": true,
  "noRemoteServers": false,
  "noEntriesResponseCode": 3,
  "dockerSolverHostMachineFallbackActive": true,
  "solverRemote": {
    "circuitBreaker": {
      "strategy": "CANARY_RATE_THRESHOLD",
      "failureRateThreshold": 21,
      "minimumNumberOfCalls": 50,
      "permittedNumberOfCallsInHalfOpenState": 10
    }
  }
}
```

* **failureRateThreshold**: If the failure rate is equal to or greater than this threshold, the CircuitBreaker will
  transition to open. rules: values greater than 0 and not greater than 100.
* **minimumNumberOfCalls**: Configures the minimum number of calls which are required (per sliding window period) before
  the CircuitBreaker can calculate the error rate.
* **permittedNumberOfCallsInHalfOpenState**: Configures the number of permitted calls when the CircuitBreaker is half
  open.

## Refs

* [A more resilient circuit breaker strategy #533][2]

[1]: https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html#toString--
[2]: https://github.com/mageddo/dns-proxy-server/issues/533
