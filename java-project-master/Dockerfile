FROM eclipse-temurin:11-jre-jammy

WORKDIR /app

COPY target/java11-1.0.0.jar app.jar

VOLUME /app/logs

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
