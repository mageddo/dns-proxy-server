#### Disconnect containers from network

```
DELETE /network/disconnect-containers HTTP/1.1
```

__Parameters__

| Name       	| Type   	| Decription                                                   	|
|------------	|--------	|--------------------------------------------------------------	|
| netoworkId 	| string 	| The networkId which the containers will be disconnected from 	|

__Reponse__

```javascript
HTTP/1.1 200
[
    "success for 551adbb704bf95ae73f3f8e497560609d2016d1566196298f1787f087af4b5cd",
    "success for 5f5ce51404b15069795006e7319f270db4f6d822a067e61808eeb0b2922087db"
]
```

__Example__

```bash
$ curl -X DELETE dns.mageddo:5380/network/disconnect-containers/?networkId=85e7564c6b71
```
