FROM debian:8
ADD build/dns-proxy-server-linux-amd64*.tgz /app/
WORKDIR /app
VOLUME ["/var/run/docker.sock", "/var/run/docker.sock"]
CMD ["bash", "-c", "/app/dns-proxy-server"]
