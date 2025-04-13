-- Changeset 009 author: vlad.mosuyk
-- Add created_at and updated_at columns to media table

DO $$
BEGIN
    -- Проверка существования таблицы media
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'media') THEN
        -- Проверка существования колонки created_at
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'media' AND column_name = 'created_at') THEN
            -- Добавление колонки created_at
            ALTER TABLE media 
            ADD COLUMN created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
            
            -- Установка значения по умолчанию для существующих записей
            UPDATE media SET created_at = CURRENT_TIMESTAMP;
        END IF;
        
        -- Проверка существования колонки updated_at
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'media' AND column_name = 'updated_at') THEN
            -- Добавление колонки updated_at
            ALTER TABLE media 
            ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
            
            -- Установка значения по умолчанию для существующих записей
            UPDATE media SET updated_at = CURRENT_TIMESTAMP;
        END IF;
    END IF;
END $$;
