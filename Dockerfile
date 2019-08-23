FROM debian:10-slim
ADD build/dns-proxy-server-linux-amd64*.tgz /app/
WORKDIR /app
LABEL dps.container=true
VOLUME ["/var/run/docker.sock", "/var/run/docker.sock"]
CMD ["bash", "-c", "/app/dns-proxy-server"]
