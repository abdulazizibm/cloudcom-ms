FROM eclipse-temurin:21-jre-jammy

WORKDIR /app-dir

ARG UID=10001
ARG USER=app-user

LABEL maintainer="abdulazizibm68@gmail.com"
LABEL app="CloudCom Microservices Platform"
LABEL repo="https://github.com/abdulazizibm/cloudcom-ms"

RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    ${USER}

USER app-user

COPY target/gateway-service-jar-with-dependencies-exec.jar/ app.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "app.jar" ]
