-- Changeset 005 author: assistant
-- Add created_at and updated_at columns to actions table

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'actions') THEN
        -- Проверка существования колонки created_at
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                       WHERE table_name = 'actions' AND column_name = 'created_at') THEN
            ALTER TABLE actions 
            ADD COLUMN created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;
        END IF;
        
        -- Проверка существования колонки updated_at
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                       WHERE table_name = 'actions' AND column_name = 'updated_at') THEN
            ALTER TABLE actions 
            ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
        END IF;
    END IF;
END
$$; 