-- Liquibase formatted SQL

-- Master changelog file
-- В данном файле собраны все миграции в порядке их выполнения

-- Инициализация Liquibase
\i classpath:db/changelog/sql/000-init-liquibase.sql

-- Включение миграции 001-initial-schema.sql (основная схема)
\i classpath:db/changelog/sql/001-initial-schema.sql

-- Включение миграции 002-add-campaign-fields.sql
\i classpath:db/changelog/sql/002-add-campaign-fields.sql

-- Включение миграции 003-fix-messages-table.sql
\i classpath:db/changelog/sql/003-fix-messages-table.sql

-- Включение миграции 004-check-correct-structure.sql (проверка и исправление структуры)
\i classpath:db/changelog/sql/004-check-correct-structure.sql 