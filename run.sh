#!/bin/bash

# Загрузка переменных окружения из .env
export $(grep -v '^#' .env | xargs)

# Добавляем недостающие переменные
export SPRING_JWT_SECRET="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
export SPRING_JWT_HASH_SECRET="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
export SPRING_EMAIL_ADDRESS="test@example.com"
export SPRING_EMAIL_PASSWORD="password"
export WORKSPACE_API="http://workspace-service:8080"
export CONTEXT_PATH="/api"

# Запуск приложения в тестовом режиме с пропуском тестов
SPRING_PROFILES_ACTIVE=test mvn clean spring-boot:run -DskipTests 