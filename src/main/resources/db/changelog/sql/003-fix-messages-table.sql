-- Changeset 003 author: fix-messages-schema
-- Исправление таблицы messages и связанных таблиц

-- Проверка существования таблицы obj_pools
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'obj_pools') 
    AND NOT EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'messages') THEN
        -- Переименование таблицы obj_pools в messages
        ALTER TABLE obj_pools RENAME TO messages;
    END IF;
END
$$;

-- Проверка существования столбца channel_id в таблице messages
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                  WHERE table_name = 'messages' AND column_name = 'channel_id') THEN
        -- Добавление колонки channel_id в таблицу messages
        ALTER TABLE messages ADD COLUMN channel_id UUID;
    END IF;
END
$$;

-- Обновление ссылок в таблице медиа
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_media_message') THEN
        -- Удаление существующего внешнего ключа
        ALTER TABLE medias DROP CONSTRAINT IF EXISTS fk_media_message;
        
        -- Добавление нового внешнего ключа
        ALTER TABLE medias 
            ADD CONSTRAINT fk_media_message 
            FOREIGN KEY (message_id) 
            REFERENCES messages(id) 
            ON DELETE CASCADE;
    END IF;
END
$$;

-- Обновление ссылок в таблице действий
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_action_message') THEN
        -- Удаление существующего внешнего ключа
        ALTER TABLE actions DROP CONSTRAINT IF EXISTS fk_action_message;
        
        -- Добавление нового внешнего ключа
        ALTER TABLE actions 
            ADD CONSTRAINT fk_action_message 
            FOREIGN KEY (message_id) 
            REFERENCES messages(id) 
            ON DELETE CASCADE;
    END IF;
END
$$;

-- Запись в DATABASECHANGELOG для отслеживания миграции
INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID)
VALUES ('003', 'fix-messages-schema', 'classpath:db/changelog/sql/003-fix-messages-table.sql', NOW(), 3, '8:1234567890abcdef', 'sql', 'Исправление таблицы messages и связанных таблиц', 'EXECUTED', NULL, NULL, '4.20.0', CONCAT('SQL-', CAST(NOW() AS VARCHAR))); 