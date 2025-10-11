-- Schema Migration Script for AML System
-- This script updates the database schema to match the new entity structure

-- ============================================
-- TRANSACTION TABLE MODIFICATIONS
-- ============================================

-- Add new columns to transactions table
ALTER TABLE transactions 
ADD COLUMN sender_account_number VARCHAR(50) NOT NULL DEFAULT 'TEMP';

ALTER TABLE transactions 
ADD COLUMN currency_exchange_id BIGINT NULL;

-- Add foreign key constraint for currency_exchange_id
ALTER TABLE transactions 
ADD CONSTRAINT fk_transactions_currency_exchange 
FOREIGN KEY (currency_exchange_id) REFERENCES currency_exchange(id);

-- Create index for currency_exchange_id
CREATE INDEX idx_transactions_currency_exchange ON transactions(currency_exchange_id);

-- Remove old columns from transactions table
ALTER TABLE transactions DROP COLUMN sender_account_id;
ALTER TABLE transactions DROP COLUMN receiver_account_id;
ALTER TABLE transactions DROP COLUMN conversion_id;
ALTER TABLE transactions DROP COLUMN conversion_fee;
ALTER TABLE transactions DROP COLUMN original_currency;
ALTER TABLE transactions DROP COLUMN original_amount;
ALTER TABLE transactions DROP COLUMN exchange_rate;
ALTER TABLE transactions DROP COLUMN is_currency_converted;

-- Remove the temporary default constraint
ALTER TABLE transactions ALTER COLUMN sender_account_number DROP DEFAULT;

-- ============================================
-- USERS TABLE MODIFICATIONS
-- ============================================

-- Remove OTP related columns from users table
ALTER TABLE users DROP COLUMN verification_otp;
ALTER TABLE users DROP COLUMN otp_expiry_time;

-- ============================================
-- UPDATE EXISTING DATA (if needed)
-- ============================================

-- Update existing transactions to have proper sender_account_number
-- This is a placeholder - you may need to customize based on your existing data
UPDATE transactions t 
SET sender_account_number = COALESCE(
    (SELECT a.account_number FROM accounts a WHERE a.id = t.sender_account_id), 
    'EXTERNAL'
)
WHERE sender_account_number = 'TEMP';

-- ============================================
-- VERIFY SCHEMA CHANGES
-- ============================================

-- Verify transactions table structure
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'transactions' 
ORDER BY ORDINAL_POSITION;

-- Verify users table structure  
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'users' 
ORDER BY ORDINAL_POSITION;

-- ============================================
-- NOTES
-- ============================================
-- 1. Run this script in a transaction to allow rollback if needed
-- 2. Backup your database before running this migration
-- 3. Test thoroughly in a development environment first
-- 4. The sender_acc_number field now stores account numbers directly
-- 5. Currency conversion data is now referenced via currency_exchange_id
-- 6. OTP functionality has been removed from User entity
