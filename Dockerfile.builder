FROM node:12-alpine AS BUILDER
COPY app /app
WORKDIR /app
ENV PUBLIC_URL=/static
RUN npm install &&\
	npm run build &&\
	rm -f `find ./build -name *.map`

FROM golang:1.12.9 AS GOLANG
RUN apt update &&\
	apt install -y jq &&\
	curl -s -L https://github.com/mageddo-projects/github-cli/releases/download/v1.4/github-cli.sh > /usr/bin/github-cli &&\
	chmod +x /usr/bin/github-cli
ENV GOPATH=/app
ENV MG_WORK_DIR=/app/src/github.com/mageddo/dns-proxy-server
LABEL dps.container=true
WORKDIR /app/src/github.com/mageddo/dns-proxy-server
COPY --from=BUILDER /app/build /static
COPY ./builder.bash /bin/builder.bash
