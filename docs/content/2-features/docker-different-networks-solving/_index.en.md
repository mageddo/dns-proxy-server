---
title: Different Docker Networks Solving
---

DPS can lead with docker containers with different networks, it will find the target container best matching IP,
best matching means the IP which have the most chances of be accessible from the client,
no distinction if the client is another container or not.

You can [click here][1] to see a pratical working sample.

The following table describles the possible scenarios involving docker container networks, some scenarios won't work,
you can consider enable [DPS Docker Network feature][1] so all contains will always be able to talk with each other.

| DPS Container Network | Client    | Client Network     | Target Container Network |    Result    | Description                                                                                                                                                                                                                                                                                                             |
|-----------------------|-----------|--------------------|--------------------------|:------------:|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Any bridge network    | Container | any bridge network | any bridge network       |      OK      | Everything works fine when all the networks are using bridge driver, no problem if the networks are different                                                                                                                                                                                                           |
| Network x             | Container | Network x          | Network x                |      OK      | Even with restrictive networks (like overlay driver) the resolution will work, since all containers are connected to this same network.                                                                                                                                                                                 |
| Network a, Network b  | Container | Network a          | Network b                | PARTIALLY OK | Resolution is OK but Client Container can't ping Target Container. The minimal requirement is that client must be  on the same network which DPS so the client will able to query DPS, then client also need to be on the same network which the target container, so it will be able to ping after solve the hostname. |
| Network a             | Container | Network b          | Network b                |     NOK      | Client container won't be able to talk with DPS                                                                                                                                                                                                                                                                         |
| Any bridge network    | Host      | Any                | any bridge network       |      OK      | Host can ping to DPS and Ping the target solved container IP                                                                                                                                                                                                                                                            |
| Any bridge network    | Host      | Any                | Not on a Bridge Network  | PARTIALLY OK | Host can ping to DPS but can't Ping the target solved container IP                                                                                                                                                                                                                                                      |
| Network a             | Host      | Any                | Any                      |     FAIL     | Host can't ping to DPS, so DNS resolution won't work on the host Machine, you can bypass that by publishing DPS container port to the host machine but is only possible on Linux                                                                                                                                        |

[1]:  {{%relref "2-features/dps-network-resolution/_index.md" %}}
