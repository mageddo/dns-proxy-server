---
title: Stress Tests
weight: 4
pre: "<b>4. </b>"
---

Start DPS Instance to be tested

```bash
docker-compose -f src/stress-test/docker/dps-stress-test-instance/docker-compose.yml up --build
```

Start Grafana Stack to Collect Performance Metrics

```bash
docker-compose -f src/stress-test/docker/grafana/docker-compose.yml up --build
```

Run the Stress Test Suite

```bash 
./gradlew build stressTest
```


[1]: {{%relref "1-getting-started/requirements/_index.en.md" %}}
