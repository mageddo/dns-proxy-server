---
title: Different Docker Networks Solving
---

DPS can lead with docker containers with different networks, it will find the target container best matching IP,
best matching means the IP which have the most chances of be accessible from the client,
no distinction if the client is another container or not.

You can [click here][1] to see a pratical working sample.

The following table describles the possible scenarios involving docker container networks, some scenarios won't work,
you can consider enable [DPS Docker Network feature][1] so all contains will always be able to talk with each other.

| DPS Container Network | Client Container Network | Target Container Network |    Result    | Description                                                                                                                                                                                                                                                                                                             |
|-----------------------|--------------------------|--------------------------|:------------:|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| any bridge network    | any bridge network       | any bridge network       |      OK      | Everything works fine when all the networks are using bridge driver, no problem if the networks are different                                                                                                                                                                                                           |
| Network x             | Network x                | Network x                |      OK      | Even with restrictive networks (like overlay driver) the resolution will work, since all containers are connected to this same network.                                                                                                                                                                                 |
| Network a, Network b  | Network a                | Network b                | PARTIALLY OK | Resolution is OK but Client Container can't ping Target Container. The minimal requirement is that client must be  on the same network which DPS so the client will able to query DPS, then client also need to be on the same network which the target container, so it will be able to ping after solve the hostname. |
| Network a             | Network b                | Network b                |     NOK      | Client container won't be able to talk with DPS                                                                                                                                                                                                                                                                         |

[1]:  {{%relref "2-features/dps-network-resolution/_index.md" %}}
