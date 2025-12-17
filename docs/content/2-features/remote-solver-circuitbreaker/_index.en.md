---
title: Remote Solver Circuit Breaker
---

DPS use circuit breaker strategies to choose the most available Remote Server from the configured ones.

## Canary Rate Threshold
* Consider all remote servers circuits as **open** on app start
* Opens and closes circuits based on percentage of failure

#### Consider all remote servers circuits as open on app start
Test them on startup and add the healthy ones as HALF_OPEN this will evict to app get resolution fails right on the
start because the first server on the remote servers list is offline.

Activated by `solver.remote.circuitBreaker.type=CANARY_RATE_THRESHOLD`

| Name                                                                 | Description                                                                                                                                                                              | Default Value |
| -------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------- |
| `solver.remote.circuitBreaker.failureRateThreshold`                  | Failure rate percentage which, when equal to or greater than this value, causes the CircuitBreaker to transition to open. Valid values are greater than 0 and less than or equal to 100. | `21`          |
| `solver.remote.circuitBreaker.minimumNumberOfCalls`                  | Minimum number of calls required (per sliding window) before the CircuitBreaker starts calculating the failure rate.                                                                     | `50`          |
| `solver.remote.circuitBreaker.permittedNumberOfCallsInHalfOpenState` | Number of calls allowed while the CircuitBreaker is in the half-open state.                                                                                                              | `10`          |


## Static Threshold
* Consider all remote servers circuits as **closed** on app start.
* Opens and closes circuits based on fixed number of failures or successes.

Activated by `solver.remote.circuitBreaker.type=STATIC_THRESHOLD`

| Name                                                    | Description                                                                                                                              | Default Value |
| ------------------------------------------------------- |------------------------------------------------------------------------------------------------------------------------------------------| ------------- |
| `solver.remote.circuitBreaker.failureThreshold`         | How many failed attempts are allowed before opening the circuit.                                                                         | `3`           |
| `solver.remote.circuitBreaker.failureThresholdCapacity` | How many failed attempts should be stored in the internal buffer used to evaluate failures.                                              | `10`          |
| `solver.remote.circuitBreaker.successThreshold`         | How many successful attempts are required to close the circuit after it has been opened.                                                 | `5`           |
| `solver.remote.circuitBreaker.testDelay`                | How long the system should wait before testing the circuit again after it is opened. See Duration format [documentation][1] for details. | `PT20S`       |


### Refs
* [A more resilient circuit breaker strategy #533][2]

[1]: https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html#toString--
[2]: https://github.com/mageddo/dns-proxy-server/issues/533
