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
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'messages') THEN
        -- Проверяем существование column_id в таблице
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'messages' AND column_name = 'channel_id') THEN
            -- Добавление колонки channel_id в таблицу messages
            ALTER TABLE messages ADD COLUMN channel_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000'::uuid;
        ELSE
            -- Проверка, что колонка channel_id не может быть NULL
            ALTER TABLE messages ALTER COLUMN channel_id SET NOT NULL;
            -- Устанавливаем значение по умолчанию для NULL значений
            UPDATE messages SET channel_id = '00000000-0000-0000-0000-000000000000'::uuid WHERE channel_id IS NULL;
        END IF;
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