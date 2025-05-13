--liquibase formatted sql

--changeset vladislav.mosuyk:create_campaigns_table
--comment Создание таблицы campaigns для хранения информации о рекламных кампаниях
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
    max_retargeted BIGINT,
    audience_percent INT DEFAULT 100 NOT NULL,
    max_cost DECIMAL(19,2),
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by UUID,
    error_message TEXT
);

--changeset vladislav.mosuyk:create_messages_table
--comment Создание таблицы messages для хранения креативов (сообщений)
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
    channel_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_by UUID
);

--changeset vladislav.mosuyk:create_campaign_creatives_table
--comment Создание таблицы campaign_creatives для связи кампаний и креативов
CREATE TABLE IF NOT EXISTS campaign_creatives (
    id UUID PRIMARY KEY NOT NULL,
    message_id UUID NOT NULL,
    campaign_id UUID NOT NULL,
    percent INTEGER,
    ordinal INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by UUID
);

--changeset vladislav.mosuyk:add_campaign_creatives_foreign_keys
--comment Добавление внешних ключей для таблицы campaign_creatives
ALTER TABLE campaign_creatives ADD CONSTRAINT fk_campaign_creative_message
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE;
ALTER TABLE campaign_creatives ADD CONSTRAINT fk_campaign_creative_campaign
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id) ON DELETE CASCADE;

--changeset vladislav.mosuyk:create_media_table
--comment Создание таблицы media для хранения медиа-файлов
CREATE TABLE IF NOT EXISTS media (
    id UUID PRIMARY KEY NOT NULL,
    message_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    file_name UUID NOT NULL,
    file_extension VARCHAR(10) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by UUID
);

--changeset vladislav.mosuyk:add_media_foreign_keys
--comment Добавление внешних ключей для таблицы media
ALTER TABLE media ADD CONSTRAINT fk_media_message
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE;

--changeset nightbird78:rm_messageId_from_media
--comment Удаление поля message_id из таблицы media
ALTER TABLE media DROP COLUMN message_id;

--changeset vladislav.mosuyk:create_actions_table
--comment Создание таблицы actions для хранения действий (кнопок)
CREATE TABLE IF NOT EXISTS actions (
    id UUID PRIMARY KEY NOT NULL,
    message_id UUID NOT NULL,
    text VARCHAR(255) NOT NULL,
    link VARCHAR(2048),
    ordinal INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by UUID
);

--changeset vladislav.mosuyk:add_actions_foreign_keys
--comment Добавление внешних ключей для таблицы actions
ALTER TABLE actions ADD CONSTRAINT fk_action_message
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE;

--changeset vladislav.mosuyk:create_retarget_stats_table
--comment Создание таблицы статистики ретаргетинга
CREATE TABLE IF NOT EXISTS retarget_stats (
    id UUID PRIMARY KEY NOT NULL,
    campaign_id UUID NOT NULL,
    sent_message_count INT DEFAULT 0,
    retarget_count INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    click_count INTEGER DEFAULT 0,
    target_count INTEGER DEFAULT 0,
    delivered_count INTEGER DEFAULT 0,
    created_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID
);

--changeset vladislav.mosuyk:add_retarget_stats_foreign_keys
--comment Добавление внешних ключей для таблицы retarget_stats
ALTER TABLE retarget_stats ADD CONSTRAINT fk_retarget_stats_campaign 
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id);

--changeset vladislav.mosuyk:create_retarget_stats_index
--comment Создание индекса для таблицы retarget_stats
CREATE INDEX idx_retarget_stats_campaign_created 
    ON retarget_stats(campaign_id, created_at);

--changeset vladislav.mosuyk:create_campaign_to_subscribers_table
--comment Создание таблицы связи кампаний с подписчиками
CREATE TABLE IF NOT EXISTS campaign_to_subscribers (
    id UUID PRIMARY KEY NOT NULL,
    campaign_id UUID NOT NULL,
    subscriber_id UUID NOT NULL,
    creative_id UUID,
    retargeted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

--changeset vladislav.mosuyk:add_campaign_to_subscribers_foreign_keys
--comment Добавление внешних ключей для таблицы campaign_to_subscribers
ALTER TABLE campaign_to_subscribers ADD CONSTRAINT fk_campaign_to_subscribers_campaign 
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id);
ALTER TABLE campaign_to_subscribers ADD CONSTRAINT fk_campaign_to_subscribers_creative 
    FOREIGN KEY (creative_id) REFERENCES messages(id);

--changeset vladislav.mosuyk:create_campaign_to_subscribers_indexes
--comment Создание индексов для таблицы campaign_to_subscribers
CREATE INDEX idx_campaign_to_subscribers_campaign_subscriber 
    ON campaign_to_subscribers(campaign_id, subscriber_id);
CREATE INDEX idx_campaign_to_subscribers_retargeted 
    ON campaign_to_subscribers(retargeted);

--changeset vladislav.mosuyk:create_media_to_message_table
--comment Создание таблицы связи медиа с сообщениями
CREATE TABLE IF NOT EXISTS media_to_message (
    id UUID PRIMARY KEY NOT NULL,
    media_id UUID NOT NULL,
    message_id UUID NOT NULL
);

--changeset vladislav.mosuyk:add_media_to_message_foreign_keys
--comment Добавление внешних ключей для таблицы media_to_message
ALTER TABLE media_to_message ADD CONSTRAINT fk_media_to_message_media
    FOREIGN KEY (media_id) REFERENCES media(id);
ALTER TABLE media_to_message ADD CONSTRAINT fk_media_to_message_message
    FOREIGN KEY (message_id) REFERENCES messages(id);

--changeset vladislav.mosuyk:create_partner_links_table
--comment Создание таблицы partner_links для хранения партнерских ссылок
CREATE TABLE IF NOT EXISTS partner_links (
    id UUID PRIMARY KEY NOT NULL,
    original_url VARCHAR(2048) NOT NULL,
    workspace_id UUID NOT NULL,
    created_by UUID NOT NULL,
    campaign_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

--changeset vladislav.mosuyk:add_partner_links_foreign_keys
--comment Добавление внешних ключей для таблицы partner_links
ALTER TABLE partner_links ADD CONSTRAINT fk_partner_links_campaign
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id);

--changeset vladislav.mosuyk:create_partner_link_clicks_table
--comment Создание таблицы partner_link_clicks для хранения кликов по партнерским ссылкам
CREATE TABLE IF NOT EXISTS partner_link_clicks (
    id UUID PRIMARY KEY NOT NULL,
    partner_link_id UUID NOT NULL,
    user_id UUID NOT NULL,
    clicked_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

--changeset vladislav.mosuyk:add_partner_link_clicks_foreign_keys
--comment Добавление внешних ключей для таблицы partner_link_clicks
ALTER TABLE partner_link_clicks ADD CONSTRAINT fk_partner_link_clicks_partner_link
    FOREIGN KEY (partner_link_id) REFERENCES partner_links(id);

--changeset vladislav.mosuyk:create_click_events_table
--comment Создание таблицы click_events для хранения детальной информации о кликах
CREATE TABLE IF NOT EXISTS click_events (
    id UUID PRIMARY KEY NOT NULL,
    partner_link_id UUID NOT NULL,
    user_id UUID NOT NULL,
    ip_address VARCHAR(45),
    browser VARCHAR(100),
    browser_version VARCHAR(50),
    operating_system VARCHAR(100),
    device_type VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

--changeset vladislav.mosuyk:add_click_events_foreign_keys
--comment Добавление внешних ключей для таблицы click_events
ALTER TABLE click_events ADD CONSTRAINT fk_click_events_partner_link
    FOREIGN KEY (partner_link_id) REFERENCES partner_links(id);

--changeset vladislav.mosuyk:create_click_events_indexes
--comment Создание индексов для таблицы click_events
CREATE INDEX idx_click_events_partner_link ON click_events(partner_link_id);
CREATE INDEX idx_click_events_user ON click_events(user_id);
CREATE INDEX idx_click_events_created_at ON click_events(created_at);