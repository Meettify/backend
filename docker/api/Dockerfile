# JDK 이미지를 사용하여 애플리케이션 실행
FROM openjdk:17-jdk-slim

# 빌드된 JAR 파일의 경로
ARG JAR_FILE=build/libs/meettify-0.0.1-SNAPSHOT.jar
EXPOSE 8080

# JAR 파일을 컨테이너에 복사
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app.jar"]
