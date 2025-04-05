-- Changeset 004 author: vladislav.mosuyk
-- Проверка и исправление структуры базы данных

-- Проверка существования всех необходимых таблиц и корректности их структуры

-- 1. Проверка и исправление таблицы messages
DO $$
BEGIN
    -- Проверка существования таблицы messages
    IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'messages') THEN
        -- Создаем таблицу сообщений если она не существует
        CREATE TABLE messages (
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
    ELSE
        -- Проверка и добавление недостающих колонок в таблицу messages
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'messages' AND column_name = 'channel_id') THEN
            ALTER TABLE messages ADD COLUMN channel_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000'::uuid;
        END IF;
        
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'messages' AND column_name = 'created_at') THEN
            ALTER TABLE messages ADD COLUMN created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;
        END IF;
        
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'messages' AND column_name = 'updated_at') THEN
            ALTER TABLE messages ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;
        END IF;
    END IF;
END
$$;

-- 2. Проверка и исправление таблицы campaigns
DO $$
BEGIN
    -- Проверка существования таблицы campaigns
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'campaigns') THEN
        -- Проверка и добавление недостающих колонок
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'campaigns' AND column_name = 'channel_id') THEN
            ALTER TABLE campaigns ADD COLUMN channel_id UUID;
        END IF;
    END IF;
END
$$;

-- 3. Проверка необходимых внешних ключей
DO $$
BEGIN
    -- Проверка и добавление внешних ключей для таблицы media
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'media') 
       AND EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'messages') THEN
        -- Проверка существования внешнего ключа
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_media_message') THEN
            ALTER TABLE media 
                ADD CONSTRAINT fk_media_message 
                FOREIGN KEY (message_id) 
                REFERENCES messages(id) 
                ON DELETE CASCADE;
        END IF;
    END IF;
    
    -- Проверка и добавление внешних ключей для таблицы actions
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'actions') 
       AND EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'messages') THEN
        -- Проверка существования внешнего ключа
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_action_message') THEN
            ALTER TABLE actions 
                ADD CONSTRAINT fk_action_message 
                FOREIGN KEY (message_id) 
                REFERENCES messages(id) 
                ON DELETE CASCADE;
        END IF;
    END IF;
END
$$; 