# Builder stage
FROM gradle:8.0-jdk17 AS builder
WORKDIR /app
COPY . .
RUN ggradle clean build --no-build-cache

# JDK stage
FROM openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/meettify-0.0.1-SNAPSHOT.jar
COPY --from=builder /app/${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app.jar"]
