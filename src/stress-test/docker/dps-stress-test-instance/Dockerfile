FROM debian:12

ARG PROCESS_EXPORTER_URL=https://github.com/ncabatoff/process-exporter/releases/download/v0.8.2/process-exporter-0.8.2.linux-amd64.tar.gz

RUN mkdir /process-exporter && mkdir -p /var/log/supervisord &&\
    apt-get update && apt-get install -y curl supervisor &&\
    curl -sL ${PROCESS_EXPORTER_URL} > /tmp/process-exporter.tgz &&\
    tar --strip 1 -zxvf /tmp/process-exporter.tgz -C /process-exporter

# from website
ARG APP_URL=https://github.com/mageddo/dns-proxy-server/releases/download/3.24.0-snapshot/dns-proxy-server-linux-amd64-3.24.0-snapshot.tgz
RUN mkdir /app &&\
    curl -sL ${APP_URL} > /tmp/app.tgz &&\
    tar --strip 1 -zxvf /tmp/app.tgz -C /app

# from local build
#RUN mkdir /app
#COPY files/dns-proxy-server /app/dns-proxy-server

WORKDIR /process-exporter

COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf
COPY conf.yaml /process-exporter/conf.yaml

ENTRYPOINT ["/usr/bin/supervisord"]
