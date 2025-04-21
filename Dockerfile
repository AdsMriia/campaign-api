FROM maven:3.8.4-openjdk-11-slim AS build
WORKDIR /app

# Копируем файлы pom.xml
COPY pom.xml .

# Загружаем зависимости отдельно от кода для лучшего кэширования
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY . .

# Собираем приложение, пропуская тесты для ускорения сборки
RUN mvn package 

# Финальный образ
FROM openjdk:11-jre-slim
WORKDIR /app

# Устанавливаем зависимости для TdLib, если необходимо
RUN apt-get update && apt-get install -y \
    libssl-dev \
    zlib1g-dev \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Копируем JAR из этапа сборки
COPY --from=build /app/target/*.jar app.jar

# Порт, который будет доступен извне
EXPOSE 8080

# Точка входа для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"] 