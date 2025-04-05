#!/bin/bash

# Скрипт для запуска миграций базы данных

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# Загрузка переменных окружения из .env файла, если он существует
if [ -f .env ]; then
    echo -e "${YELLOW}Загрузка переменных окружения из .env файла...${NC}"
    export $(grep -v '^#' .env | xargs)
fi

# Проверка наличия необходимых переменных окружения
if [ -z "$SPRING_DATASOURCE_URL" ] || [ -z "$SPRING_DATASOURCE_USERNAME" ] || [ -z "$SPRING_DATASOURCE_PASSWORD" ]; then
    echo -e "${RED}Ошибка: Не заданы переменные окружения для подключения к базе данных.${NC}"
    echo "Установите переменные SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME и SPRING_DATASOURCE_PASSWORD"
    exit 1
fi

# Извлечение данных для подключения к базе
DB_URL=$(echo $SPRING_DATASOURCE_URL | sed -E 's/.*:\/\/([^\/]+)\/([^?]+).*/\1\/\2/')
DB_HOST=$(echo $DB_URL | cut -d'/' -f1)
DB_NAME=$(echo $DB_URL | cut -d'/' -f2)

echo -e "${YELLOW}Подключение к базе данных $DB_NAME на $DB_HOST...${NC}"

# Запуск миграции через maven
echo -e "${YELLOW}Выполнение миграций базы данных...${NC}"
mvn liquibase:update

# Проверка статуса
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Миграции успешно выполнены!${NC}"
    
    # Вывод текущего статуса миграций
    echo -e "${YELLOW}Текущий статус миграций:${NC}"
    PGPASSWORD=$SPRING_DATASOURCE_PASSWORD psql -h $DB_HOST -U $SPRING_DATASOURCE_USERNAME -d $DB_NAME -f src/main/resources/db/changelog/sql/check-status.sql
else
    echo -e "${RED}Ошибка выполнения миграций!${NC}"
    exit 1
fi 