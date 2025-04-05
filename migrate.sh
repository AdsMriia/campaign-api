#!/bin/bash

# Скрипт для запуска миграций базы данных

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# Функция справки
show_help() {
    echo "Использование: $0 [опции]"
    echo ""
    echo "Опции:"
    echo "  -r, --reset     Полный сброс базы данных перед миграцией"
    echo "  -c, --check     Только проверка статуса миграций"
    echo "  -h, --help      Показать эту справку"
    echo ""
}

# Обработка аргументов командной строки
RESET_DB=false
CHECK_ONLY=false

while [[ $# -gt 0 ]]
do
    key="$1"
    case $key in
        -r|--reset)
            RESET_DB=true
            shift
            ;;
        -c|--check)
            CHECK_ONLY=true
            shift
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        *)
            echo -e "${RED}Неизвестная опция: $1${NC}"
            show_help
            exit 1
            ;;
    esac
done

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

# Если выбран режим сброса, очищаем базу
if [ "$RESET_DB" = true ]; then
    echo -e "${RED}ВНИМАНИЕ: Выполняется полный сброс базы данных!${NC}"
    read -p "Вы уверены, что хотите продолжить? [y/N] " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${YELLOW}Выполняется сброс базы данных...${NC}"
        PGPASSWORD=$SPRING_DATASOURCE_PASSWORD psql -h $DB_HOST -U $SPRING_DATASOURCE_USERNAME -d $DB_NAME -c "
        DROP SCHEMA public CASCADE;
        CREATE SCHEMA public;
        GRANT ALL ON SCHEMA public TO postgres;
        GRANT ALL ON SCHEMA public TO public;"
        
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}База данных успешно сброшена!${NC}"
        else
            echo -e "${RED}Ошибка при сбросе базы данных!${NC}"
            exit 1
        fi
    else
        echo -e "${YELLOW}Операция сброса отменена пользователем.${NC}"
        exit 0
    fi
fi

# Если выбран режим проверки, просто показываем статус
if [ "$CHECK_ONLY" = true ]; then
    echo -e "${YELLOW}Проверка статуса миграций...${NC}"
    PGPASSWORD=$SPRING_DATASOURCE_PASSWORD psql -h $DB_HOST -U $SPRING_DATASOURCE_USERNAME -d $DB_NAME -f src/main/resources/db/changelog/sql/check-status.sql
    exit 0
fi

# Запуск миграции через maven
echo -e "${YELLOW}Выполнение миграций базы данных...${NC}"
mvn liquibase:update

# Проверка статуса
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Миграции успешно выполнены!${NC}"
    
    # Вывод текущего статуса миграций
    echo -e "${YELLOW}Текущий статус миграций:${NC}"
    PGPASSWORD=$SPRING_DATASOURCE_PASSWORD psql -h $DB_HOST -U $SPRING_DATASOURCE_USERNAME -d $DB_NAME -f src/main/resources/db/changelog/sql/check-status.sql
    
    echo -e "${GREEN}Проверка структуры базы данных...${NC}"
    PGPASSWORD=$SPRING_DATASOURCE_PASSWORD psql -h $DB_HOST -U $SPRING_DATASOURCE_USERNAME -d $DB_NAME -c "\d messages"
else
    echo -e "${RED}Ошибка выполнения миграций!${NC}"
    exit 1
fi 