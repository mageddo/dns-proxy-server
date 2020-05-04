FROM debian:10-slim
ADD build/dns-proxy-server-linux-amd64*.tgz /app/
WORKDIR /app
LABEL dps.container=true
COPY overlay /
ENTRYPOINT ["/usr/bin/docker-entrypoint"]
CMD ["/app/dns-proxy-server"]
