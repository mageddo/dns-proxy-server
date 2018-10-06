FROM arm64v8/debian:9
ADD build/dns-proxy-server-linux-arm64-*.tgz /app/
WORKDIR /app
VOLUME ["/var/run/docker.sock", "/var/run/docker.sock"]
CMD ["bash", "-c", "/app/dns-proxy-server"]
