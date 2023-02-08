FROM openjdk:17-slim
COPY ./build/dns-proxy-server-*-alpha-runner.jar /app/dns-proxy-server.jar
COPY conf/ /app/conf/
RUN #mkdir /app/conf
WORKDIR /app
ENTRYPOINT ["java", "-jar", "/app/dns-proxy-server.jar", "--log-level", "DEBUG"]
