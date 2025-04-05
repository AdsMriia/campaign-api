-- Changeset 002 author: vladislav.mosuyk
-- Adding new fields to Campaign and creating RetargetStats and CampaignToSubscribers tables

-- Добавление новых полей в таблицу кампаний
ALTER TABLE ab_tables
    ADD COLUMN audience_percent INT DEFAULT 100 NOT NULL,
    ADD COLUMN max_cost DECIMAL(19,2);

-- Добавление уникального ограничения на название кампании
ALTER TABLE ab_tables
    ADD CONSTRAINT uk_ab_tables_table_name UNIQUE (table_name);

-- Создание таблицы статистики ретаргетинга
CREATE TABLE IF NOT EXISTS retarget_stats (
    id UUID PRIMARY KEY NOT NULL,
    campaign_id UUID NOT NULL,
    sent_message_count INT DEFAULT 0,
    retarget_count INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_retarget_stats_campaign FOREIGN KEY (campaign_id) REFERENCES ab_tables(id)
);

-- Создание таблицы связи кампаний с подписчиками
CREATE TABLE IF NOT EXISTS campaign_to_subscribers (
    id UUID PRIMARY KEY NOT NULL,
    campaign_id UUID NOT NULL,
    subscriber_id UUID NOT NULL,
    creative_id UUID,
    retargeted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_campaign_to_subscribers_campaign FOREIGN KEY (campaign_id) REFERENCES ab_tables(id),
    CONSTRAINT fk_campaign_to_subscribers_creative FOREIGN KEY (creative_id) REFERENCES ab_messages(id)
);

-- Создание индексов
CREATE INDEX idx_campaign_to_subscribers_campaign_subscriber ON campaign_to_subscribers(campaign_id, subscriber_id);
CREATE INDEX idx_campaign_to_subscribers_retargeted ON campaign_to_subscribers(retargeted);
CREATE INDEX idx_retarget_stats_campaign_created ON retarget_stats(campaign_id, created_at);

-- Запись в DATABASECHANGELOG для отслеживания миграции
INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID)
VALUES ('002', 'vladislav.mosuyk', 'classpath:db/changelog/sql/002-add-campaign-fields.sql', NOW(), 2, '8:1234567890abcdef', 'sql', 'Adding new fields to Campaign and creating RetargetStats and CampaignToSubscribers tables', 'EXECUTED', NULL, NULL, '4.20.0', CONCAT('SQL-', CAST(NOW() AS VARCHAR))); 