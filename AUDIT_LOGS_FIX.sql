-- Fix audit_logs table action column size issue
-- The action column needs to be larger to accommodate longer enum values like PASSWORD_RESET_REQUEST

ALTER TABLE audit_logs MODIFY COLUMN action VARCHAR(50) NOT NULL;

-- Also ensure other string columns have adequate size
ALTER TABLE audit_logs MODIFY COLUMN ip_address VARCHAR(45) NOT NULL;
ALTER TABLE audit_logs MODIFY COLUMN user_agent VARCHAR(1000);

COMMIT;
