---
title: Local Entries Solving (LocalDB)
---
You can configure pre-defined A, AAAA, CNAME entries to solve Records, DPS will query them after docker containers
and before remote servers. You can configure them by the JSON config file or [using the GUI][1].

JSON Configuration Example

```json
{
  "activeEnv": "",
  "envs" : [{
    "name": "", // empty string is the default enviroment
    "hostnames": [{ // all local hostnames entries
      "id": 1, // (optional) used to control it will be automatically generated if not passed
      "type": "A", // Other options: CNAME, AAAA
      "hostname": "github.com",
      "ip": "192.168.0.1",
      "ttl": 255 // how many seconds cache this entry
    }]
  }]
}
```

[1]: {{%relref "2-features/gui/_index.md" %}}
