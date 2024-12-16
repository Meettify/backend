# JDK stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY ./build/libs/meettify-0.0.1-SNAPSHOT.jar app.jar
# Enable STOMP plugin
RUN rabbitmq-plugins enable --offline rabbitmq_stomp
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
