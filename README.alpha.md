## Build project

Binary using Native Image
```bash
$ ./gradlew build -Dquarkus.package.type=native &&\
   ./build/dns-proxy-server-*-runner --server-port 8053
```

# Drafts

## Run a container  for testing

```
docker run --rm -it  --hostname nginx.dev -e 'HOSTNAMES=nginx.com.br' nginx:1.15 bash
```
## x
```bash
docker-compose -f docker-compose-alpha.yml rm &&\
docker-compose -f docker-compose-alpha.yml build &&\
docker-compose -f docker-compose-alpha.yml run -T --rm arm7x86
```

Generate json reflect for all project 

```
$ ./gradlew shadowJar

$ mkdir reflect &&\
  $JAVA_HOME/bin/java -agentlib:native-image-agent=config-output-dir=./reflect -jar build/libs/dns-proxy-server*all.jar

 /home/typer/Downloads/dns-proxy-server-linux-amd64-2.19.5/dns-proxy-server -default-dns=false -server-port=5481 -web-server-port=5381 
 
 --conf-path /tmp/xpto.json

```


```
nslookup -po=8053 google.com 127.0.0.1
-Djava.net.preferIPv4Stack=true

docker network create -d bridge shibata

com.github.dockerjava.core.command.ConnectToNetworkCmdImpl
```

Configurar reflection de classes third party
```
./gradlew build -Dquarkus.package.type=uber-jar
java  -cp './build/dns-proxy-server-3.0.0-alpha-runner.jar:../annotation-processing-tools/reflection-config-generator/build/libs/reflection-config-generator-2.4.3-all.jar' nativeimage.core.thirdparty.Main 'com.github.dockerjava.core.command' tmp.json

```


docker-compose -f docker-compose-tmp.yml up --build


Implementing: https://www.ietf.org/rfc/rfc1035.txt 
