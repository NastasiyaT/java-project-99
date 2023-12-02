FROM eclipse-temurin:20-jdk

ARG GRADLE_VERSION=8.3
ENV APP_ENV

RUN apt-get update && apt-get install -yq make unzip

COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradlew .

RUN ./gradlew --no-daemon dependencies

COPY src src
COPY config config

RUN ./gradlew --no-daemon build

EXPOSE 8080

CMD java -jar build/libs/app-0.0.1-SNAPSHOT.jar
