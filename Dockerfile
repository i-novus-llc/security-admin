FROM openjdk:8-jre-alpine

LABEL maintainer="apatronov@i-novus.ru"

RUN apk add tzdata
ENV TZ=Europe/Moscow

ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar","--server.port=8080"]