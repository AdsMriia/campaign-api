-- Добавление полей created_by и updated_by в таблицы, если они отсутствуют

-- Проверка и добавление полей в таблицу campaigns
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'campaigns') THEN
        -- Проверка и добавление колонки updated_by
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'campaigns' AND column_name = 'updated_by') THEN
            ALTER TABLE campaigns ADD COLUMN updated_by UUID;
        END IF;
    END IF;
END $$;

-- Проверка и добавление полей в таблицу messages
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'messages') THEN
        -- Проверка и добавление колонки updated_by
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'messages' AND column_name = 'updated_by') THEN
            ALTER TABLE messages ADD COLUMN updated_by UUID;
        END IF;
    END IF;
END $$;

-- Проверка и добавление полей в таблицу actions
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'actions') THEN
        -- Проверка и добавление колонки created_by
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'actions' AND column_name = 'created_by') THEN
            ALTER TABLE actions ADD COLUMN created_by UUID;
        END IF;
        
        -- Проверка и добавление колонки updated_by
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'actions' AND column_name = 'updated_by') THEN
            ALTER TABLE actions ADD COLUMN updated_by UUID;
        END IF;
    END IF;
END $$;

-- Проверка и добавление полей в таблицу media
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'media') THEN
        -- Проверка и добавление колонки created_by
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'media' AND column_name = 'created_by') THEN
            ALTER TABLE media ADD COLUMN created_by UUID;
        END IF;
        
        -- Проверка и добавление колонки updated_by
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'media' AND column_name = 'updated_by') THEN
            ALTER TABLE media ADD COLUMN updated_by UUID;
        END IF;
    END IF;
END $$;

-- Проверка и добавление полей в таблицу campaign_creatives
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'campaign_creatives') THEN
        -- Проверка и добавление колонки created_by
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'campaign_creatives' AND column_name = 'created_by') THEN
            ALTER TABLE campaign_creatives ADD COLUMN created_by UUID;
        END IF;
        
        -- Проверка и добавление колонки updated_by
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'campaign_creatives' AND column_name = 'updated_by') THEN
            ALTER TABLE campaign_creatives ADD COLUMN updated_by UUID;
        END IF;
    END IF;
END $$; 