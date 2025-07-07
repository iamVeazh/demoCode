FROM openjdk:17-jdk-slim
LABEL authors="veazh"
WORKDIR /app
COPY target/service-1.0-SNAPSHOT-jar-with-dependencies.jar /app/app.jar
COPY src/main/resources/static.properties /etc/static.properties
ENTRYPOINT ["java", "-jar", "app.jar"]