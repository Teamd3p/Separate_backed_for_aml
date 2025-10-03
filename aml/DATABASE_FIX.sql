-- Fix for UserStatus column size issue
-- Run this SQL script in your MySQL database

USE aml_new;

-- Check current column definition
SHOW COLUMNS FROM users LIKE 'status';

-- Modify the status column to accommodate longer enum values
ALTER TABLE users MODIFY COLUMN status VARCHAR(50) DEFAULT 'PENDING_VERIFICATION';

-- Verify the change
SHOW COLUMNS FROM users LIKE 'status';

-- Optional: Check existing data
SELECT user_id, email, status FROM users;
