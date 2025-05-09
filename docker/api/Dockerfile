# JDK stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY ./build/libs/meettify-0.0.1-SNAPSHOT.jar app.jar
RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
RUN echo "Asia/Seoul" > /etc/timezone
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
