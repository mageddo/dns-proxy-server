## Developing
```bash
MG_WORK_DIR=$PWD ./gradlew quarkusDev
```
## Build project

Binary using Native Image
```bash
$ ./gradlew build -Dquarkus.package.type=native &&\
   ./build/dns-proxy-server-*-runner --server-port 8053
```

CD
```
docker-compose build build-frontend
```


## Generate binaries 
```
 native-image -H:+ReportExceptionStackTraces -H:Class=com.mageddo.dnsproxyserver.App  build/dns-proxy-server-3.0.0-alpha.jar 

```
libfreetype-dev
sudo apt-get install build-essential



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



## To build native image manually when using quarkus
Command copied from  `./gradlew clean build -Dquarkus.package.type=native -i -x check`

```bash
$ cd build/dns-proxy-server-native-image-source-jar
$ native-image -J-Djava.util.logging.manager=org.jboss.logmanager.LogManager -J-Dsun.nio.ch.maxUpdateArraySize=100 \
-J-Dlogging.initial-configurator.min-level=500 -J-Dio.netty.leakDetection.level=DISABLED -J-Dio.netty.allocator.maxOrder=3 \
-J-Dvertx.logger-delegate-factory-class-name=io.quarkus.vertx.core.runtime.VertxLogDelegateFactory \
-J-Dvertx.disableDnsResolver=true -J-Duser.language=en -J-Duser.country=US -J-Dfile.encoding=UTF-8 \
--features=io.quarkus.runner.Feature,io.quarkus.runtime.graal.ResourcesFeature,io.quarkus.runtime.graal.DisableLoggingFeature \
-J--add-exports=java.security.jgss/sun.security.krb5=ALL-UNNAMED -J--add-opens=java.base/java.text=ALL-UNNAMED \
-J--add-opens=java.base/java.io=ALL-UNNAMED -J--add-opens=java.base/java.lang.invoke=ALL-UNNAMED \
-J--add-opens=java.base/java.util=ALL-UNNAMED -H:+CollectImageBuildStatistics \
-H:ImageBuildStatisticsFile=dns-proxy-server-timing-stats.json \
-H:BuildOutputJSONFile=dns-proxy-server-build-output-stats.json -H:+AllowFoldMethods -J-Djava.awt.headless=true \
--no-fallback --link-at-build-time -H:+ReportExceptionStackTraces -H:-AddAllCharsets --enable-url-protocols=http \
-H:NativeLinkerOption=-no-pie -H:-UseServiceLoaderFeature -H:+StackTrace \
-J--add-exports=org.graalvm.sdk/org.graalvm.nativeimage.impl=ALL-UNNAMED \
-J--add-exports=org.graalvm.nativeimage.builder/com.oracle.svm.core.jdk=ALL-UNNAMED \
--exclude-config io\.netty\.netty-codec /META-INF/native-image/io\.netty/netty-codec/generated/handlers/reflect-config\.json \
--exclude-config io\.netty\.netty-handler /META-INF/native-image/io\.netty/netty-handler/generated/handlers/reflect-config\.json dns-proxy-server \
-jar dns-proxy-server.jar

```
