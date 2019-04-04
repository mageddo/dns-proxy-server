FROM node:8.15-jessie AS BUILDER
COPY app /app
WORKDIR /app
ENV PUBLIC_URL=/static
RUN npm run build

FROM golang:1.11
WORKDIR /app/src/github.com/mageddo/dns-proxy-server
COPY --from=BUILDER /app/build /static
