-- Fix for UserStatus column size issue and Transaction sender_account_id constraint
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

-- Fix Transaction table to allow null sender_account_id for deposits
-- Check current transactions table structure
SHOW COLUMNS FROM transactions LIKE 'sender_account_id';

-- Modify sender_account_id to allow NULL values for external deposits
ALTER TABLE transactions MODIFY COLUMN sender_account_id BIGINT NULL;

-- Also modify receiver_account_id to allow NULL values for external withdrawals
ALTER TABLE transactions MODIFY COLUMN receiver_account_id BIGINT NULL;

-- Verify the changes
SHOW COLUMNS FROM transactions WHERE Field IN ('sender_account_id', 'receiver_account_id');
