FROM debian:12

ARG PROCESS_EXPORTER_URL=https://github.com/ncabatoff/process-exporter/releases/download/v0.8.2/process-exporter-0.8.2.linux-amd64.tar.gz

RUN mkdir /process-exporter &&\
    apt-get update && apt-get install -y curl &&\
    curl -sL ${PROCESS_EXPORTER_URL} > /tmp/process-exporter.tgz &&\
    tar --strip 1 -zxvf /tmp/process-exporter.tgz -C /process-exporter

WORKDIR /process-exporter
RUN ls -lha /process-exporter
COPY conf.yaml /process-exporter/conf.yaml


CMD ["./process-exporter", "-config.path", "conf.yaml"]
