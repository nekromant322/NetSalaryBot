FROM maven:3.8.4-openjdk-17 AS build

COPY src /usr/src/app/src
COPY pom.xml /usr/src/app

RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:17

WORKDIR /telegramBot

COPY --from=build /usr/src/app/target/telegramBot-0.0.1-SNAPSHOT.jar /telegramBot/

EXPOSE 8080

CMD ["java", "-jar", "telegramBot-0.0.1-SNAPSHOT.jar"]