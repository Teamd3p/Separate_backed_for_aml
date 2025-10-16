-- Password Reset Token Migration Script
-- This script creates the password_reset_tokens table for secure password reset functionality

-- Create password_reset_tokens table
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    used_at DATETIME NULL,
    ip_address VARCHAR(45) NULL,
    user_agent VARCHAR(500) NULL,
    
    -- Foreign key constraint
    CONSTRAINT fk_password_reset_user 
        FOREIGN KEY (user_id) REFERENCES users(user_id) 
        ON DELETE CASCADE,
    
    -- Indexes for performance
    INDEX idx_password_reset_token (token),
    INDEX idx_password_reset_user_id (user_id),
    INDEX idx_password_reset_expiry (expiry_date),
    INDEX idx_password_reset_used (used),
    INDEX idx_password_reset_created (created_at)
);

-- Add comments for documentation
ALTER TABLE password_reset_tokens 
COMMENT = 'Stores secure password reset tokens with expiry and usage tracking';

ALTER TABLE password_reset_tokens 
MODIFY COLUMN token VARCHAR(255) NOT NULL UNIQUE 
COMMENT 'Secure URL-safe token for password reset';

ALTER TABLE password_reset_tokens 
MODIFY COLUMN user_id BIGINT NOT NULL 
COMMENT 'Reference to the user requesting password reset';

ALTER TABLE password_reset_tokens 
MODIFY COLUMN expiry_date DATETIME NOT NULL 
COMMENT 'Token expiration timestamp (typically 1 hour from creation)';

ALTER TABLE password_reset_tokens 
MODIFY COLUMN used BOOLEAN NOT NULL DEFAULT FALSE 
COMMENT 'Flag indicating if token has been used';

ALTER TABLE password_reset_tokens 
MODIFY COLUMN ip_address VARCHAR(45) NULL 
COMMENT 'IP address from which reset was requested';

ALTER TABLE password_reset_tokens 
MODIFY COLUMN user_agent VARCHAR(500) NULL 
COMMENT 'User agent string from reset request';

-- Insert initial configuration if needed
-- (No initial data required for password reset tokens)

COMMIT;
