FROM adoptopenjdk/openjdk14:jre


EXPOSE 8080

ARG JAR_FILE
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","app.jar","--server.port=8080"]