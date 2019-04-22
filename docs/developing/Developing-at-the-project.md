### Developing with docker

	$ docker-compose rm -f && docker-compose up --build app-dps compiler-dps

Running the application 

```
$ docker-compose exec compiler-dps bash
$ go run dns.go
```

Running the GUI

```
$ docker-compose exec app-dps bash
$ npm start
```

Running unit tests

	$ go test -cover=false ./.../

### Developing at Intellij 
* [Install Golang plugin](https://github.com/go-lang-plugin-org)
* Import the project at `File -> New -> Project from existing sources`
* Make solve dependencies 
    * `File -> Settings -> Languages & Frameworks -> Go -> Go Libraries`
    * At Project Libraries section add the project folder
![](http://pix.toile-libre.org/upload/original/1499630100.png)


* Running: At Intellij you will want to make some customizes like these:
    * Custom port to evict conflicts and don't need root privleges
    * Custom config path
    * Don't set it up at the default DNS
![](http://i.imgur.com/gCUCndC.jpg)

