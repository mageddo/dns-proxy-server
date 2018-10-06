FROM arm32v7/debian:7
ADD build/dns-proxy-server-linux-arm-*.tgz /app/
WORKDIR /app
VOLUME ["/var/run/docker.sock", "/var/run/docker.sock"]
CMD ["bash", "-c", "/app/dns-proxy-server"]
