# Base image for building the application
FROM gradle:8.0.2-jdk17 AS builder
WORKDIR /app

# 소스 코드 복사 (Git 클론 대신 GitHub Actions에서 클론한 코드를 사용)
COPY . .

# Gradle 빌드 수행
RUN chmod +x gradlew && ./gradlew build --no-daemon

# Base image for running the application
FROM openjdk:17-jdk-slim

ARG JAR_FILE=build/libs/meettify-0.0.1-SNAPSHOT.jar
EXPOSE 8080

COPY --from=builder /app/${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app.jar"]
