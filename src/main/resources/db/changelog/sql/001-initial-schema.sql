-- Changeset 001 author: vladislav.mosuyk
-- Creating initial database schema

-- Создание таблицы campaigns
CREATE TABLE IF NOT EXISTS campaigns (
    id UUID PRIMARY KEY NOT NULL,
    title VARCHAR(255) NOT NULL,
    start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by UUID NOT NULL,
    campaign_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    workspace_id UUID NOT NULL,
    channel_id UUID NOT NULL,
    is_archived BOOLEAN DEFAULT FALSE NOT NULL,
    max_retargeted BIGINT
);

-- Создание таблицы messages
CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY NOT NULL,
    mark_down BOOLEAN DEFAULT FALSE NOT NULL,
    workspace_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    telegram_id BIGINT,
    text TEXT NOT NULL,
    created_by UUID NOT NULL,
    channel_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Создание таблицы campaign_creatives
CREATE TABLE IF NOT EXISTS campaign_creatives (
    id UUID PRIMARY KEY NOT NULL,
    message_id UUID NOT NULL,
    campaign_id UUID NOT NULL,
    percent INTEGER,
    ordinal INTEGER NOT NULL
);

-- Создание таблицы media
CREATE TABLE IF NOT EXISTS media (
    id UUID PRIMARY KEY NOT NULL,
    message_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    file_name UUID NOT NULL,
    file_extension VARCHAR(10) NOT NULL
);

-- Создание таблицы actions
CREATE TABLE IF NOT EXISTS actions (
    id UUID PRIMARY KEY NOT NULL,
    message_id UUID NOT NULL,
    text VARCHAR(255) NOT NULL,
    link VARCHAR(2048) NOT NULL,
    ordinal INTEGER NOT NULL
);

-- Добавление внешних ключей
ALTER TABLE campaign_creatives
    ADD CONSTRAINT fk_campaign_creative_message
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE;

ALTER TABLE campaign_creatives
    ADD CONSTRAINT fk_campaign_creative_campaign
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id) ON DELETE CASCADE;

ALTER TABLE media
    ADD CONSTRAINT fk_media_message
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE;

ALTER TABLE actions
    ADD CONSTRAINT fk_action_message
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE;

-- Запись в DATABASECHANGELOG для отслеживания миграции
INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID)
VALUES ('001', 'vladislav.mosuyk', 'classpath:db/changelog/sql/001-initial-schema.sql', NOW(), 1, '8:1234567890abcdef', 'sql', 'Creating initial database schema', 'EXECUTED', NULL, NULL, '4.20.0', CONCAT('SQL-', CAST(NOW() AS VARCHAR))); 