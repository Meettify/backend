# 빌드 스테이지
FROM openjdk:17-jdk-slim AS build

WORKDIR /app

# 의존성 파일 복사
COPY gradle/wrapper/ gradle/wrapper/
COPY build.gradle settings.gradle /app/

# 의존성 설치
RUN ./gradlew build --parallel --no-daemon

# 소스 코드 복사
COPY src /app/src

# 빌드 실행
RUN ./gradlew build -x test --no-daemon

# 최종 이미지
FROM openjdk:17-jdk-slim

WORKDIR /app

# 빌드된 JAR 파일만 복사
COPY --from=build /app/build/libs/meettify-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
