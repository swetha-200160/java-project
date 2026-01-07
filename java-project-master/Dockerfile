# 1. Use Java runtime
FROM openjdk:17-jdk-slim

# 2. Set container directory
WORKDIR /app

# 3. Copy built JAR
COPY target/*.jar app.jar

# 4. Expose app port
EXPOSE 8080

# 5. Start app
ENTRYPOINT ["java","-jar","app.jar"]
