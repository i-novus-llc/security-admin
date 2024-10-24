ARG BASE_IMAGE=harbor.i-novus.ru/library/redos7c/jdk-17:17.0.10-1.30.0
FROM $BASE_IMAGE AS builder

ARG JAR_FILE=target/access-web.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM $BASE_IMAGE
EXPOSE 8080

COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]