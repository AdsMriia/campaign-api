-- Changeset 010 author: vlad.mosuyk
-- Add missing columns to retarget_stats table

DO $$
BEGIN
    -- Проверка существования таблицы retarget_stats
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'retarget_stats') THEN
        -- Добавление колонки click_count
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'retarget_stats' AND column_name = 'click_count') THEN
            ALTER TABLE retarget_stats 
            ADD COLUMN click_count INTEGER DEFAULT 0;
        END IF;
        
        -- Добавление колонки target_count
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'retarget_stats' AND column_name = 'target_count') THEN
            ALTER TABLE retarget_stats 
            ADD COLUMN target_count INTEGER DEFAULT 0;
        END IF;
        
        -- Добавление колонки delivered_count
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'retarget_stats' AND column_name = 'delivered_count') THEN
            ALTER TABLE retarget_stats 
            ADD COLUMN delivered_count INTEGER DEFAULT 0;
        END IF;
        
        -- Добавление колонки created_by
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'retarget_stats' AND column_name = 'created_by') THEN
            ALTER TABLE retarget_stats 
            ADD COLUMN created_by UUID;
        END IF;
        
        -- Добавление колонки updated_at
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'retarget_stats' AND column_name = 'updated_at') THEN
            ALTER TABLE retarget_stats 
            ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
            
            -- Установка значения по умолчанию для существующих записей
            UPDATE retarget_stats SET updated_at = created_at WHERE updated_at IS NULL;
        END IF;
        
        -- Добавление колонки updated_by
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'retarget_stats' AND column_name = 'updated_by') THEN
            ALTER TABLE retarget_stats 
            ADD COLUMN updated_by UUID;
        END IF;
    END IF;
END $$;
