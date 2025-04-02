--liquibase formatted sql

--changeset root:create_plans_table
--comment 1. Create plans table
CREATE TABLE plans (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    max_admins INT,
    max_channels INT,
    price float,
    currency VARCHAR(3),
    description TEXT,
    is_default BOOLEAN
);

--changeset root:create_permissions_table:
--comment 2. Create permissions table
CREATE TABLE permissions (
    id UUID PRIMARY KEY,
    name VARCHAR(255)
);

--changeset root:create_subscriptions_table
CREATE TABLE subscriptions (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    max_admins INT,
    max_channels INT,
    status VARCHAR(255),
    expires_at timestamp with time zone,
    created_at timestamp with time zone
);

--changeset root:create_subscriptions_to_permissions
CREATE  TABLE subscriptions_to_permissions (
    id UUID PRIMARY KEY,
    subscription_id UUID REFERENCES subscriptions(id),
    permission_id UUID REFERENCES permissions(id)
);

--changeset root:create_web_users_table
--comment 3. Create web_users table
CREATE TABLE web_users (
    id UUID PRIMARY KEY,
    email VARCHAR(255),
    subscribe_id UUID REFERENCES subscriptions(id),
    password VARCHAR(255),
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    telegram_id BIGINT,
    username VARCHAR(255),
    telegram_username VARCHAR(255),
    expire_at timestamp with time zone
);
--changeset root:add-unique-telegram-id-constraint-web_users
ALTER TABLE web_users ADD CONSTRAINT unique_telegram_id UNIQUE (telegram_id);

--changeset root:create_work_spaces_table
--comment 4. Create work_spaces table
CREATE TABLE work_spaces (
    id UUID PRIMARY KEY,
    active BOOLEAN,
    owner_id UUID REFERENCES web_users(id)
);

--changeset root:create_web_stats_table
--comment 5. Create web_stats table
CREATE TABLE web_stats (
    id UUID PRIMARY KEY,
    channel_id BIGINT,
    local_id INT,
    global_id BIGINT,
    view_count INT,
    reaction_count INT,
    reply_count INT,
    last_view_update timestamp with time zone,
    last_reaction_update timestamp with time zone,
    last_reply_update timestamp with time zone
);

--changeset root:create_subscribers_table
--comment 6. Create subscribers table
CREATE TABLE subscribers (
    id UUID PRIMARY KEY,
    telegram_id BIGINT
);
--changeset root:add-unique-telegram-id-constraint-subscribers
ALTER TABLE subscribers ADD CONSTRAINT unique_telegram_id_subscribers UNIQUE (telegram_id);

--changeset root:add_column_to_subscribers_table
ALTER TABLE subscribers ADD COLUMN username VARCHAR(255);
ALTER TABLE subscribers ADD COLUMN first_name VARCHAR(255);
ALTER TABLE subscribers ADD COLUMN last_name VARCHAR(255);
ALTER TABLE subscribers ADD COLUMN language_code VARCHAR(255);
--changeset root:add_column_to_subscribers_table_last_private_message_id
ALTER TABLE subscribers ADD COLUMN last_private_message_id BIGINT;

--changeset root:create_channels_table
--comment 8. Create channels table
CREATE TABLE channels (
    id UUID PRIMARY KEY,
    channel_id BIGINT,
    chat_id BIGINT,
    title VARCHAR(255)
);
--changeset root:add_unique_channel_id_constraint
ALTER TABLE channels ADD CONSTRAINT unique_channel_id UNIQUE (channel_id);
--changeset root:add_column_to_channels_table
ALTER TABLE channels ADD COLUMN channel_link TEXT;

--changeset root:create_obj_pools_table
--comment 7. Create obj_pools table
CREATE TABLE obj_pools (
    id UUID PRIMARY KEY,
    workspace_id UUID REFERENCES work_spaces(id),
    channel_id UUID REFERENCES channels(id),
    created_by UUID REFERENCES web_users(id),
    telegram_id BIGINT,
    text VARCHAR(255),
    type VARCHAR(255),
    status VARCHAR(255),
    created_at timestamp with time zone,
    updated_at timestamp with time zone
);
--changeset root:edit-text-column-in-obj-pools-table
ALTER TABLE obj_pools ALTER COLUMN text TYPE VARCHAR(65535);
--changeset root:add-ab-column-to-obj-pools-table
ALTER TABLE obj_pools ADD COLUMN ab boolean;
--changeset root:rename-ab-column-to-mark_down
ALTER TABLE obj_pools RENAME COLUMN ab TO mark_down;
--changeset root:add-title-column-to-obj-pools-table
ALTER TABLE obj_pools ADD COLUMN title VARCHAR(255);

--changeset root:create_actions_table
--comment 9. Create actions table
CREATE TABLE actions (
    id UUID PRIMARY KEY,
    obj_pool_id UUID REFERENCES obj_pools(id),
    text TEXT,
    link VARCHAR(255)
);

--changeset root:edit-text-column-in-actions-table
ALTER TABLE actions ALTER COLUMN text TYPE VARCHAR(255);

--changeset root:add-ordinal-column-to-actions-table
ALTER TABLE actions ADD COLUMN ordinal INT;

--changeset root:create_actions_to_subscribers_table
--comment 10. Create actions_to_subscribers table
CREATE TABLE actions_to_subscribers (
    id UUID PRIMARY KEY,
    subscriber_id UUID REFERENCES subscribers(id),
    action_id UUID REFERENCES actions(id)
);

--changeset root:create_admin_pools_table
--comment 11. Create admin_pools table
CREATE TABLE admin_pools (
    id UUID PRIMARY KEY,
    workspace_id UUID REFERENCES work_spaces(id),
    web_user_id UUID REFERENCES web_users(id),
    enabled BOOLEAN
);

--changeset root:create_bots
--comment 24. Create bots
CREATE TABLE bots (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    token VARCHAR(255),
    is_default BOOLEAN DEFAULT FALSE
);

--changeset root:add_bot_type_column_to_bots
ALTER TABLE bots ADD COLUMN bot_type VARCHAR(255);
--changeset root:add_workspace_id_column_to_bots
ALTER TABLE bots ADD COLUMN workspace_id UUID REFERENCES work_spaces(id);

--changeset root:create_channel_pools_table
--comment 12. Create channel_pools table
CREATE TABLE channel_pools (
    id UUID PRIMARY KEY,
    workspace_id UUID REFERENCES work_spaces(id),
    channel_id UUID REFERENCES channels(id),
    tracking BOOLEAN
);

--changeset root:add_column_to_channel_pools_table
ALTER TABLE channel_pools ADD COLUMN bot_id UUID REFERENCES bots(id);

--changeset root:create_channel_subscribers_table
--comment 13. Create channel_subscribers table
CREATE TABLE channel_subscribers (
    id UUID PRIMARY KEY,
    subscriber_id UUID REFERENCES subscribers(id),
    channel_id UUID REFERENCES channels(id),
    leave_time timestamp with time zone,
    join_time timestamp with time zone,
    last_message_time timestamp with time zone
);
--changeset root:rename_last_message_time
ALTER TABLE channel_subscribers RENAME COLUMN last_message_time TO last_interaction_time;

--changeset root:create_medias_table
--comment 14. Create medias table
CREATE TABLE medias (
    id UUID PRIMARY KEY,
    obj_pool_id UUID REFERENCES obj_pools(id),
    workspace_id UUID REFERENCES work_spaces(id),
    file_name UUID,
    file_extension VARCHAR(255)
);

--changeset root:create_plans_to_permissions_table
--comment 15. Create plans_to_permissions table
CREATE TABLE plans_to_permissions (
    id UUID PRIMARY KEY,
    plan_id UUID REFERENCES plans(id),
    permission_id UUID REFERENCES permissions(id)
);

--changeset root:create_web_stats_historys_table
--comment 16. Create web_stats_historys table
CREATE TABLE web_stats_historys (
    id UUID PRIMARY KEY,
    web_stats_id UUID REFERENCES web_stats(id),
    last_view_update timestamp with time zone,
    last_reaction_update timestamp with time zone,
    last_reply_update timestamp with time zone,
    view_count INT,
    reaction_count INT,
    reply_count INT
);
--changeset root:add_created_at_column_to_web_stats_historys_table
ALTER TABLE web_stats_historys ADD COLUMN created_at timestamp with time zone;

--changeset root:create_otp_table
--comment 17. Create otp table
CREATE TABLE otp (
    id UUID PRIMARY KEY,
    otp BIGINT,
    user_id BIGINT,
    user_name VARCHAR(255),
    telegram_username VARCHAR(255),
    expiry_time TIMESTAMP
);

--changeset root:create_admins_table
--comment 18. Create admins table
CREATE TABLE admins (
    id UUID PRIMARY KEY,
    channel_id UUID REFERENCES channels(id),
    telegram_id BIGINT
);
--changeset root:add-unique-telegram-id-constraint-admins
ALTER TABLE admins ADD CONSTRAINT unique_telegram_id_admins UNIQUE (telegram_id);

--changeset root:create_ButtonChannelLink
--comment 19. Create ButtonChannelLink table | shit
create table ButtonChannelLink (
    id UUID PRIMARY KEY,
    channel_id UUID,
    button_id UUID,
    created_at timestamp with time zone
);

--changeset root:create_button_channel_link
ALTER TABLE ButtonChannelLink RENAME TO button_channel_link;
--changeset root:change-button_id-to-action_id
ALTER TABLE button_channel_link RENAME COLUMN button_id TO action_id

--changeset root:create_ab_table
--comment 20. Create ab table
CREATE TABLE ab_tables (
    id UUID PRIMARY KEY,
    end_date timestamp with time zone,
    start_date timestamp with time zone,
    active boolean,
    channel_id UUID REFERENCES channels(id),
    workspace_id UUID REFERENCES work_spaces(id)
);
--changeset root:add_column_to_ab_tables
ALTER TABLE ab_tables ADD COLUMN table_name VARCHAR(255);
--changeset root:rename_active
ALTER TABLE ab_tables RENAME COLUMN active TO company_status;
--changeset root:rename_active_type
ALTER TABLE ab_tables ALTER COLUMN company_status TYPE VARCHAR(255);
--changeset root:add_active_column_to_ab_tables
ALTER TABLE ab_tables ADD COLUMN is_archived boolean;
--changeset root:add_created_at_column_to_ab_tables
ALTER TABLE ab_tables ADD COLUMN created_at timestamp with time zone;
--changeset root:add_created_by_column_to_ab_tables
ALTER TABLE ab_tables ADD COLUMN created_by UUID REFERENCES web_users(id);
--changeset root:add_type_column_to_ab_tables
ALTER TABLE ab_tables ADD COLUMN company_type VARCHAR(255);

--changeset root:create_ab_message
--comment 21. Create ab message
CREATE TABLE ab_messages (
    id UUID PRIMARY KEY,
    message_id UUID REFERENCES obj_pools(id),
    ab_table_id UUID REFERENCES ab_tables(id),
    percent INT,
    ordinal INT
);

--changeset root:create_ab_message_to_subscriber
--comment 22. Create ab message to subscriber
CREATE TABLE ab_test_to_subscribers (
    id UUID PRIMARY KEY,
    ab_message_id UUID REFERENCES ab_messages(id),
    subscriber_id UUID REFERENCES subscribers(id),
    created_at timestamp with time zone
);
--changeset root:add_column_to_ab_test_to_subscribers
ALTER TABLE ab_test_to_subscribers ADD COLUMN retargeted boolean;

--changeset root:retarget_stats
--comment 23. Create retarget stats
CREATE TABLE retarget_stats (
    id UUID PRIMARY KEY,
    ab_table_id UUID REFERENCES ab_tables(id),
    created_at timestamp with time zone,
    sent_message_count INT,
    participant_count INT,
    retarget_count INT,
    retarget_by_system INT,
    retarget_by_user INT
);

--changeset root:create_bots_to_users
--comment 25. Create bots to users
CREATE TABLE bots_to_users (
    id UUID PRIMARY KEY,
    bot_id UUID REFERENCES bots(id),
    subscriber_id UUID REFERENCES subscribers(id)
);

--changeset root:add_column_can_send_message_to_bots_to_users
ALTER TABLE bots_to_users ADD COLUMN can_send_message boolean;

--changeset root:create_links_table
--comment 26. Create links table
CREATE TABLE links (
    id UUID PRIMARY KEY,
    link TEXT
);

--changeset root:create_payments_table
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    amount DECIMAL,
    shop_order_id VARCHAR(255),
    status VARCHAR(50),
    comment TEXT,
    transaction_id BIGINT
);

--changeset root:add_created_at_column_to_payments_table
ALTER TABLE payments ADD COLUMN created_at timestamp with time zone;

--changeset root:create_balance_table
CREATE TABLE balance (
    id UUID PRIMARY KEY,
    amount DECIMAL,
    user_id UUID REFERENCES web_users(id)
);

--changeset root:create_file_processing_logs_table
CREATE TABLE file_processing_logs (
    id UUID PRIMARY KEY,                                        -- Уникальный идентификатор
    workspace_id UUID REFERENCES work_spaces(id),               -- Идентификатор рабочего пространства
    status VARCHAR(255),                                        -- Статус обработки файла
    message TEXT,                                               -- Сообщение о состоянии обработки
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,               -- Временная метка создания записи
    total_elements INT DEFAULT 0,                               -- Общее количество элементов
    parsed_elements INT DEFAULT 0,                              -- Количество обработанных элементов
    original_file_name VARCHAR(255),                            -- Исходное название файла
    system_file_name VARCHAR(255)                               -- Системное название файла (присвоено программой)
);

--changeset root:add_bot_column_to_file_processing_logs
ALTER TABLE file_processing_logs ADD COLUMN bot_id UUID REFERENCES bots(id);

--comment 27. Create passwords_reset table
--changeset root:create_passwords_reset
CREATE TABLE passwords_reset (
    id UUID PRIMARY KEY,
    web_user_id UUID REFERENCES web_users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

--changeset root:create_bot_to_bot
CREATE TABLE bot_to_bot (
    id UUID PRIMARY KEY,
    subscriber_id UUID REFERENCES subscribers(id),
    bot_from UUID REFERENCES bots(id),
    bot_to UUID REFERENCES bots(id),
    successes BOOLEAN
);
--changeset root:add_from_message_id_column_to_bot_to_bot
ALTER TABLE bot_to_bot ADD COLUMN from_message_id INT;