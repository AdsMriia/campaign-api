-- Changeset 001 author: vladislav.mosuyk
-- Creating initial database schema

-- Создание таблицы кампаний
CREATE TABLE IF NOT EXISTS ab_tables (
    id UUID PRIMARY KEY NOT NULL,
    table_name VARCHAR(255) NOT NULL,
    end_date TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    start_date TIMESTAMP WITH TIME ZONE,
    created_by UUID,
    company_type VARCHAR(50),
    company_status VARCHAR(50),
    workspace_id UUID,
    channel_id UUID,
    is_archived BOOLEAN DEFAULT FALSE,
    max_retargeted BIGINT
);

-- Создание таблицы сообщений
CREATE TABLE IF NOT EXISTS obj_pools (
    id UUID PRIMARY KEY NOT NULL,
    mark_down BOOLEAN DEFAULT FALSE,
    workspace_id UUID,
    title VARCHAR(255),
    type VARCHAR(50),
    status VARCHAR(50),
    telegram_id INTEGER,
    text TEXT,
    created_by UUID,
    channel_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

-- Создание таблицы связи кампаний и креативов
CREATE TABLE IF NOT EXISTS ab_messages (
    id UUID PRIMARY KEY NOT NULL,
    message_id UUID NOT NULL,
    ab_table_id UUID NOT NULL,
    percent INTEGER,
    ordinal INTEGER,
    CONSTRAINT fk_ab_message_message FOREIGN KEY (message_id) REFERENCES obj_pools(id),
    CONSTRAINT fk_ab_message_table FOREIGN KEY (ab_table_id) REFERENCES ab_tables(id)
);

-- Создание таблицы медиафайлов
CREATE TABLE IF NOT EXISTS medias (
    id UUID PRIMARY KEY NOT NULL,
    message_id UUID NOT NULL,
    workspace_id UUID,
    file_name UUID,
    file_extension VARCHAR(50),
    CONSTRAINT fk_media_message FOREIGN KEY (message_id) REFERENCES obj_pools(id)
);

-- Создание таблицы действий
CREATE TABLE IF NOT EXISTS actions (
    id UUID PRIMARY KEY NOT NULL,
    message_id UUID NOT NULL,
    text VARCHAR(255),
    link VARCHAR(500),
    ordinal INTEGER DEFAULT 0,
    CONSTRAINT fk_action_message FOREIGN KEY (message_id) REFERENCES obj_pools(id)
);

-- Запись в DATABASECHANGELOG для отслеживания миграции
INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID)
VALUES ('001', 'vladislav.mosuyk', 'classpath:db/changelog/sql/001-init-schema.sql', NOW(), 1, '8:1234567890abcdef', 'sql', 'Creating initial database schema', 'EXECUTED', NULL, NULL, '4.20.0', CONCAT('SQL-', CAST(NOW() AS VARCHAR))); 