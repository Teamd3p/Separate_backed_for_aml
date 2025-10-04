-- Simple fix to allow null values for external transactions and fix alert column size
-- Run this SQL script in your MySQL database

USE aml_new;

-- Allow null values for sender_account_id (for deposits)
ALTER TABLE transactions MODIFY COLUMN sender_account_id BIGINT NULL;

-- Allow null values for receiver_account_id (for withdrawals)  
ALTER TABLE transactions MODIFY COLUMN receiver_account_id BIGINT NULL;

-- Fix alerts table - increase rule_triggered column size to handle longer rule names/descriptions
ALTER TABLE alerts MODIFY COLUMN rule_triggered TEXT;

-- Verify the changes
SHOW COLUMNS FROM transactions WHERE Field IN ('sender_account_id', 'receiver_account_id');
SHOW COLUMNS FROM alerts LIKE 'rule_triggered';
