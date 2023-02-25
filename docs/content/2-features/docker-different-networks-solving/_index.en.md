---
title: Different Docker Networks Solving
weight: 3
---

DPS can lead with docker containers with different networks, it will find the target container best matching IP, 
best matching means the IP which have the most chances of be accessible from the client, 
no distinction if the client is another container or not.

You can [click here][1] to see a pratical working sample.

The following table describles the possible scenarios involving docker container networks, this is an automatic decision
made by DPS, if you want to enforce some network resolution you can use `dps.network`
[label]({{%relref "2-features/specify-from-which-network-solve-container/_index.md" %}}) .

| DPS Container Network | Client Container Network | Target Container Network | Result | Description                                                                                                                                                                                                                                                                                                                                                             |
|-----------------------|--------------------------|--------------------------|:------:|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| any:bridge            | any:bridge               | any:bridge               |   OK   | Everything works fine when all the networks are using bridge driver, no problem if the networks are different                                                                                                                                                                                                                                                           |
| Network x             | Network x                | Network x                |   OK   | Even with restrictive networks (like overlay driver) the resolution will work, since all containers are connected to this same network.                                                                                                                                                                                                                                 |
| Network a, Network b  | Network a                | Network b                |   OK   | The minimal requirement is that client must be  on the same network which DPS so the client will able to query DPS, then client also need to be on the same network which the target container, so it will be able to ping after solve the hostname, DPS don't really need to be able to talk with the target container since it won't turn into a client a some moment |


[1]: https://github.com/mageddo/dns-proxy-server/tree/master/examples/docker-different-networks/
