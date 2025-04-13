-- Добавление полей created_at и updated_at в таблицу campaign_creatives
DO $$
BEGIN
    -- Проверка существования таблицы campaign_creatives
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'campaign_creatives') THEN
        
        -- Проверка существования колонки created_at
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'campaign_creatives' AND column_name = 'created_at') THEN
            ALTER TABLE campaign_creatives 
            ADD COLUMN created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;
            
            -- Установка значения по умолчанию для существующих записей
            UPDATE campaign_creatives SET created_at = CURRENT_TIMESTAMP;
        END IF;
        
        -- Проверка существования колонки updated_at
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'campaign_creatives' AND column_name = 'updated_at') THEN
            ALTER TABLE campaign_creatives 
            ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
        END IF;
        
    END IF;
END $$; 