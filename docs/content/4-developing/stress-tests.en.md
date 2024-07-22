---
title: Stress Tests
weight: 4
pre: "<b>4. </b>"
---

Start DPS Instance to be tested

```bash
docker-compose -f src/stress-test/docker/docker-compose.yaml up  --build
```

Start Grafana Stack to Collect Performance Metrics

```bash
...
```

Run the Stress Test Suite

```bash 
./gradlew build stressTest
```


[1]: {{%relref "1-getting-started/requirements/_index.en.md" %}}
