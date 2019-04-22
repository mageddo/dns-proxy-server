FROM node:8.15-jessie AS BUILDER
COPY app /app
WORKDIR /app
ENV PUBLIC_URL=/static
RUN npm install &&\
	npm run build &&\
	rm -f `find ./build -name *.map`

FROM golang:1.11 AS GOLANG
ENV GOPATH=/app
ENV MG_WORK_DIR=/app/src/github.com/mageddo/dns-proxy-server
WORKDIR /app/src/github.com/mageddo/dns-proxy-server
COPY --from=BUILDER /app/build /static
COPY ./builder.bash /bin/builder.bash
