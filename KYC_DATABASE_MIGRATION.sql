-- KYC Database Migration Script
-- This script updates the existing kyc_documents table with enhanced fields
-- and creates necessary indexes for optimal performance

-- Update kyc_documents table with new fields
ALTER TABLE kyc_documents 
ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'PENDING',
ADD COLUMN IF NOT EXISTS file_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS file_size BIGINT,
ADD COLUMN IF NOT EXISTS mime_type VARCHAR(100),
ADD COLUMN IF NOT EXISTS document_number VARCHAR(100),
ADD COLUMN IF NOT EXISTS expiry_date DATETIME,
ADD COLUMN IF NOT EXISTS issue_date DATETIME,
ADD COLUMN IF NOT EXISTS issuing_authority VARCHAR(255),
ADD COLUMN IF NOT EXISTS verification_notes TEXT,
ADD COLUMN IF NOT EXISTS verified_by BIGINT,
ADD COLUMN IF NOT EXISTS verification_timestamp DATETIME,
ADD COLUMN IF NOT EXISTS confidence_score DOUBLE,
ADD COLUMN IF NOT EXISTS risk_score INT,
ADD COLUMN IF NOT EXISTS requires_manual_review BOOLEAN DEFAULT FALSE;

-- Rename existing columns to match new naming convention
ALTER TABLE kyc_documents 
CHANGE COLUMN uploadTimestamp upload_timestamp DATETIME,
CHANGE COLUMN isValidated is_validated BOOLEAN,
CHANGE COLUMN extractedText extracted_text LONGTEXT,
CHANGE COLUMN docType doc_type VARCHAR(50),
CHANGE COLUMN fileUrl file_url VARCHAR(500);

-- Add foreign key constraint for verified_by field
ALTER TABLE kyc_documents 
ADD CONSTRAINT fk_kyc_verified_by 
FOREIGN KEY (verified_by) REFERENCES compliance_officers(id) 
ON DELETE SET NULL;

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_kyc_customer_id ON kyc_documents(customer_id);
CREATE INDEX IF NOT EXISTS idx_kyc_status ON kyc_documents(status);
CREATE INDEX IF NOT EXISTS idx_kyc_doc_type ON kyc_documents(doc_type);
CREATE INDEX IF NOT EXISTS idx_kyc_upload_timestamp ON kyc_documents(upload_timestamp);
CREATE INDEX IF NOT EXISTS idx_kyc_expiry_date ON kyc_documents(expiry_date);
CREATE INDEX IF NOT EXISTS idx_kyc_risk_score ON kyc_documents(risk_score);
CREATE INDEX IF NOT EXISTS idx_kyc_manual_review ON kyc_documents(requires_manual_review);
CREATE INDEX IF NOT EXISTS idx_kyc_verified_by ON kyc_documents(verified_by);
CREATE INDEX IF NOT EXISTS idx_kyc_customer_status ON kyc_documents(customer_id, status);
CREATE INDEX IF NOT EXISTS idx_kyc_customer_doctype ON kyc_documents(customer_id, doc_type);

-- Update existing records with default values
UPDATE kyc_documents 
SET status = 'PENDING' 
WHERE status IS NULL;

UPDATE kyc_documents 
SET file_name = SUBSTRING_INDEX(file_url, '/', -1)
WHERE file_name IS NULL AND file_url IS NOT NULL;

UPDATE kyc_documents 
SET is_validated = FALSE 
WHERE is_validated IS NULL;

UPDATE kyc_documents 
SET requires_manual_review = FALSE 
WHERE requires_manual_review IS NULL;

-- Create a view for KYC compliance summary
CREATE OR REPLACE VIEW kyc_compliance_summary AS
SELECT 
    c.id as customer_id,
    CONCAT(c.first_name, ' ', c.last_name) as customer_name,
    COUNT(k.id) as total_documents,
    SUM(CASE WHEN k.status = 'VERIFIED' THEN 1 ELSE 0 END) as verified_documents,
    SUM(CASE WHEN k.status = 'PENDING' THEN 1 ELSE 0 END) as pending_documents,
    SUM(CASE WHEN k.status = 'REJECTED' THEN 1 ELSE 0 END) as rejected_documents,
    SUM(CASE WHEN k.status = 'EXPIRED' THEN 1 ELSE 0 END) as expired_documents,
    SUM(CASE WHEN k.risk_score >= 70 THEN 1 ELSE 0 END) as high_risk_documents,
    SUM(CASE WHEN k.requires_manual_review = TRUE THEN 1 ELSE 0 END) as manual_review_documents,
    CASE 
        WHEN SUM(CASE WHEN k.status = 'VERIFIED' THEN 1 ELSE 0 END) >= 2 
             AND COUNT(DISTINCT CASE WHEN k.status = 'VERIFIED' AND k.doc_type IN ('PAN', 'AADHAAR', 'PASSPORT', 'DRIVING_LICENSE', 'VOTER_ID') THEN k.doc_type END) >= 2
        THEN 'COMPLETE'
        ELSE 'INCOMPLETE'
    END as kyc_status
FROM customers c
LEFT JOIN kyc_documents k ON c.id = k.customer_id
GROUP BY c.id, c.first_name, c.last_name;

-- Insert sample KYC rules data if rules table exists
INSERT IGNORE INTO rules (name, description, type, is_active, risk_score_impact) VALUES
('KYC Incomplete Check', 'Flags transactions from customers with incomplete KYC', 'KYC', TRUE, 60),
('Expired Document Check', 'Flags transactions from customers with expired KYC documents', 'KYC', TRUE, 80),
('High Risk Document Check', 'Flags transactions from customers with high-risk KYC documents', 'KYC', TRUE, 90),
('Pending Verification Check', 'Flags transactions from customers with long-pending KYC verifications', 'KYC', TRUE, 70),
('Rejected Document Check', 'Flags transactions from customers with rejected KYC documents', 'KYC', TRUE, 85);

-- Create stored procedure for KYC compliance check
DELIMITER //
CREATE PROCEDURE CheckKycCompliance(IN customer_id BIGINT)
BEGIN
    DECLARE kyc_complete BOOLEAN DEFAULT FALSE;
    DECLARE verified_count INT DEFAULT 0;
    DECLARE required_docs_count INT DEFAULT 0;
    
    -- Count verified documents
    SELECT COUNT(*) INTO verified_count
    FROM kyc_documents 
    WHERE customer_id = customer_id AND status = 'VERIFIED';
    
    -- Count required document types that are verified
    SELECT COUNT(DISTINCT doc_type) INTO required_docs_count
    FROM kyc_documents 
    WHERE customer_id = customer_id 
    AND status = 'VERIFIED' 
    AND doc_type IN ('PAN', 'AADHAAR', 'PASSPORT', 'DRIVING_LICENSE', 'VOTER_ID');
    
    -- Determine if KYC is complete
    IF verified_count >= 2 AND required_docs_count >= 2 THEN
        SET kyc_complete = TRUE;
    END IF;
    
    SELECT 
        customer_id,
        verified_count,
        required_docs_count,
        kyc_complete as is_kyc_complete;
END //
DELIMITER ;

-- Create trigger to automatically update customer risk profile when KYC documents change
DELIMITER //
CREATE TRIGGER update_customer_risk_on_kyc_change
AFTER UPDATE ON kyc_documents
FOR EACH ROW
BEGIN
    DECLARE avg_risk_score DECIMAL(5,2);
    
    -- Calculate average risk score for customer's documents
    SELECT AVG(COALESCE(risk_score, 0)) INTO avg_risk_score
    FROM kyc_documents 
    WHERE customer_id = NEW.customer_id;
    
    -- Update customer's risk rating based on KYC document risk scores
    UPDATE customers 
    SET risk_rating = CASE 
        WHEN avg_risk_score >= 70 THEN 'HIGH'
        WHEN avg_risk_score >= 40 THEN 'MEDIUM'
        ELSE 'LOW'
    END
    WHERE id = NEW.customer_id;
END //
DELIMITER ;

-- Add application configuration for file upload
INSERT IGNORE INTO application_config (config_key, config_value, description) VALUES
('kyc.file.upload.max-size', '10485760', 'Maximum file size for KYC document upload in bytes (10MB)'),
('kyc.file.upload.allowed-types', 'image/jpeg,image/jpg,image/png,image/gif,application/pdf', 'Allowed MIME types for KYC document upload'),
('kyc.file.upload.directory', 'uploads/kyc-documents', 'Directory path for storing KYC documents'),
('kyc.verification.auto-approve-threshold', '90', 'Confidence score threshold for auto-approval'),
('kyc.verification.manual-review-threshold', '70', 'Risk score threshold for manual review requirement'),
('kyc.compliance.minimum-documents', '2', 'Minimum number of verified documents required for complete KYC'),
('kyc.document.expiry-warning-days', '30', 'Number of days before expiry to show warning');

COMMIT;

-- Display migration completion message
SELECT 'KYC Database Migration Completed Successfully!' as Status;
