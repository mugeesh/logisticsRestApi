FROM maven:3.8.7-openjdk-18 AS maven
LABEL MAINTAINER="mugeesh@gmail.com"

#to run the integration test on Mac M1 PC
#ENV DOCKER_HOST=tcp://host.docker.internal:2375
#ENV TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal
# docker run -p 2375:2375 -v /var/run/docker.sock://var/run/docker.sock alpine/socat tcp-listen:2375,reuseaddr,fork unix-connect:/var/run/docker.sock

COPY .. .

RUN mvn clean package -DskipTests

# For Java 17
FROM openjdk:17-jdk-slim

ARG JAR_FILE=logistics-api.jar

WORKDIR /

COPY --from=maven /target/${JAR_FILE} .

ENTRYPOINT ["java","-jar","logistics-api.jar"]