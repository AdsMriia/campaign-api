-- Changeset 002 author: vladislav.mosuyk
-- Adding new fields to Campaign and creating RetargetStats and CampaignToSubscribers tables

-- Добавление новых полей в таблицу кампаний
ALTER TABLE campaigns
    ADD COLUMN audience_percent INT DEFAULT 100 NOT NULL,
    ADD COLUMN max_cost DECIMAL(19,2);

-- Добавление уникального ограничения на название кампании
ALTER TABLE campaigns
    ADD CONSTRAINT uk_campaigns_title UNIQUE (title);

-- Создание таблицы статистики ретаргетинга
CREATE TABLE IF NOT EXISTS retarget_stats (
    id UUID PRIMARY KEY NOT NULL,
    campaign_id UUID NOT NULL,
    sent_message_count INT DEFAULT 0,
    retarget_count INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_retarget_stats_campaign FOREIGN KEY (campaign_id) REFERENCES campaigns(id)
);

-- Создание таблицы связи кампаний с подписчиками
CREATE TABLE IF NOT EXISTS campaign_to_subscribers (
    id UUID PRIMARY KEY NOT NULL,
    campaign_id UUID NOT NULL,
    subscriber_id UUID NOT NULL,
    creative_id UUID,
    retargeted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_campaign_to_subscribers_campaign FOREIGN KEY (campaign_id) REFERENCES campaigns(id),
    CONSTRAINT fk_campaign_to_subscribers_creative FOREIGN KEY (creative_id) REFERENCES messages(id)
);

-- Создание индексов
CREATE INDEX idx_campaign_to_subscribers_campaign_subscriber ON campaign_to_subscribers(campaign_id, subscriber_id);
CREATE INDEX idx_campaign_to_subscribers_retargeted ON campaign_to_subscribers(retargeted);
CREATE INDEX idx_retarget_stats_campaign_created ON retarget_stats(campaign_id, created_at); 