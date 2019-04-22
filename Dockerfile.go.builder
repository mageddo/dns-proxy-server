FROM golang:1.11 AS GOLANG
ENV GOPATH=/app
ENV MG_WORK_DIR=/app/src/github.com/mageddo/dns-proxy-server
WORKDIR /app/src/github.com/mageddo/dns-proxy-server
COPY ./builder.bash /bin/builder.bash
RUN mkdir /static
