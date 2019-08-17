FROM scratch

ADD build/dns-proxy-server-linux-amd64*.tgz /app/

WORKDIR /app

VOLUME ["/var/run/docker.sock"]

ENTRYPOINT ["/app/dns-proxy-server"]
