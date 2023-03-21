FROM debian:10-slim
COPY ./build/artifacts/linux-amd64/dns-proxy-server /app/dns-proxy-server
WORKDIR /app
LABEL dps.container=true
ENV DPS_CONTAINER=1
VOLUME ["/var/run/docker.sock", "/var/run/docker.sock"]
ENTRYPOINT ["/app/dns-proxy-server"]
