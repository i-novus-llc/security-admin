FROM openjdk:8-jre-alpine

RUN apk add tzdata
ENV TZ=Europe/Moscow

EXPOSE 8080

ENV KEYCLOAK_SERVER_URL=http://localhost:8082

ARG JAR_FILE
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","app.jar","-Dkeycloak.server-url=${KEYCLOAK_SERVER_URL}"]