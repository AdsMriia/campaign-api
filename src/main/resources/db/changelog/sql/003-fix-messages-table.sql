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

-- Обновление ссылок в таблице медиа
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'medias')
    AND EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'messages')
    AND EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_media_message') THEN
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
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'actions')
    AND EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'messages')
    AND EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_action_message') THEN
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