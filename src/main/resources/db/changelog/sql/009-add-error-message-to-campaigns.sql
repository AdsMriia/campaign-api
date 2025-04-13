-- Changeset 009 author: vlad.mosuyk
-- Add error_message column to campaigns table

DO $$
BEGIN
    -- Проверка существования таблицы campaigns
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'campaigns') THEN
        -- Проверка существования колонки error_message
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'campaigns' AND column_name = 'error_message') THEN
            -- Добавление колонки error_message
            ALTER TABLE campaigns 
            ADD COLUMN error_message TEXT;
            
            -- Создаем индекс для улучшения производительности поиска по error_message
            CREATE INDEX idx_campaigns_error_message ON campaigns (error_message);
            
            -- Добавление комментария к колонке
            COMMENT ON COLUMN campaigns.error_message IS 'Сообщение об ошибке при неудачном запуске кампании';
        END IF;
    END IF;
END $$; 