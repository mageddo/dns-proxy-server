## [x] Deveria logar o endereço do servidor

10:00:21.073 [virtual-57     ] TRA c.m.d.s.r.c.c.CircuitExecutionsAsHealthChecker    l=35   m=isHealthy                       status=callFailed, answer=false, msg=java.net.SocketTimeoutException: Query timed out


## [x] Os servidores remotos deveriam começar abertos e não serem retornados no método this.findResolversToUse();

## [x] Quando a app iniciar, ela deveria tentar validar os servidores remotos para fechar os circuitos que começam abertos

## [x] testar que o circuit tem que nascer desligado

## [ ] Testes manuais gerais e abrir PR

## [ ] quebrar me mais dois prs
* o que expoe a config no json
* o que mudou comportamentos do modulo de circuit breaker do CanaryRateThresholdCircuitBreaker
