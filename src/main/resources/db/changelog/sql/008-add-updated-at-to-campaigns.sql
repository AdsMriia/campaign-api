-- Changeset 008 author: vlad.mosuyk
-- Add updated_at column to campaigns table

DO $$
BEGIN
    -- Проверка существования таблицы campaigns
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'campaigns') THEN
        -- Проверка существования колонки updated_at
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'campaigns' AND column_name = 'updated_at') THEN
            -- Добавление колонки updated_at
            ALTER TABLE campaigns 
            ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
            
            -- Установка значения по умолчанию для существующих записей
            UPDATE campaigns SET updated_at = created_at;
        END IF;
    END IF;
END $$;
