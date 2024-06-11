# Используйте официальный образ базового слоя, например, для Node.js:
FROM openjdk:latest

# Установите рабочую директорию в контейнере
WORKDIR /telegramBot

# Скопируйте исходный код проекта в контейнер
COPY ./target/telegramBot-0.0.1-SNAPSHOT.jar /telegramBot/

# Определите порт, который будет прослушивать приложение
EXPOSE 8080

# Запустите приложение при старте контейнера
CMD ["java", "-jar", "telegramBot-0.0.1-SNAPSHOT.jar"]