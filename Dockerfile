FROM docker.io/defreitas/tools_graalvm-22.3_java-19_debian-9:0.1.1 AS BUILDER
COPY ./ /app
WORKDIR /app
RUN ./gradlew clean build -Dquarkus.package.type=native -i &&\
    cd build &&\
    ls -lha &&\
    mkdir -p artifacts &&\
    mv $(ls -p ./ | grep -v / | grep dns-proxy-server) ./artifacts/

FROM debian:10-slim
COPY --from=BUILDER /app/build/artifacts/* /app/dns-proxy-server
WORKDIR /app
LABEL dps.container=true
VOLUME ["/var/run/docker.sock", "/var/run/docker.sock"]
ENTRYPOINT "/app/dns-proxy-server"
