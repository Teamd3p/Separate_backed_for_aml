-- Setup external account for handling deposits and withdrawals
-- Run this SQL script in your MySQL database

USE aml_new;

-- Create a special customer for external transactions
INSERT INTO customers (user_id, first_name, last_name, email, phone_number, address, date_of_birth, nationality, status) 
VALUES (999999, 'External', 'System', 'external@system.aml', '+00000000000', 'External System', '1900-01-01', 'SYSTEM', 'ACTIVE')
ON DUPLICATE KEY UPDATE first_name = 'External';

-- Create external accounts for different currencies
INSERT INTO accounts (account_number, customer_id, account_type, currency, balance, status, created_at) 
VALUES 
    ('EXT-SYSTEM-USD', (SELECT customer_id FROM customers WHERE user_id = 999999), 'EXTERNAL', 'USD', 999999999999.99, 'ACTIVE', NOW()),
    ('EXT-SYSTEM-EUR', (SELECT customer_id FROM customers WHERE user_id = 999999), 'EXTERNAL', 'EUR', 999999999999.99, 'ACTIVE', NOW()),
    ('EXT-SYSTEM-INR', (SELECT customer_id FROM customers WHERE user_id = 999999), 'EXTERNAL', 'INR', 999999999999.99, 'ACTIVE', NOW()),
    ('EXT-SYSTEM-GBP', (SELECT customer_id FROM customers WHERE user_id = 999999), 'EXTERNAL', 'GBP', 999999999999.99, 'ACTIVE', NOW())
ON DUPLICATE KEY UPDATE balance = 999999999999.99;

-- Verify the external accounts were created
SELECT a.account_number, a.currency, a.balance, c.first_name, c.last_name 
FROM accounts a 
JOIN customers c ON a.customer_id = c.customer_id 
WHERE c.user_id = 999999;
