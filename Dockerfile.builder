FROM node:8.15-jessie AS BUILDER
COPY app /app
WORKDIR /app
ENV PUBLIC_URL=/static
RUN npm install &&\
	npm run build &&\
	rm -f `find ./build -name *.map`

FROM golang:1.11 AS GOLANG
WORKDIR /app/src/github.com/mageddo/dns-proxy-server
COPY --from=BUILDER /app/build /static
