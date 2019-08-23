FROM golang:1.12.9 AS GOLANG
ENV GOPATH=/app
ENV MG_WORK_DIR=/app/src/github.com/mageddo/dns-proxy-server
LABEL dps.container=true
WORKDIR /app/src/github.com/mageddo/dns-proxy-server
COPY ./builder.bash /bin/builder.bash
RUN mkdir /static
