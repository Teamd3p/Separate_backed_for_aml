package com.tss.aml.entity;

import java.time.LocalDateTime;

import com.tss.aml.entity.enums.DocumentType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "kyc_documents")
public class KycDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DocumentType docType;

    @NotNull
    private String fileUrl;

    private LocalDateTime uploadTimestamp = LocalDateTime.now();
    private boolean isValidated = false;

    public KycDocument() {}

    public KycDocument(Customer customer, DocumentType docType, String fileUrl) {
        this.customer = customer;
        this.docType = docType;
        this.fileUrl = fileUrl;
    }

    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public DocumentType getDocType() { return docType; }
    public void setDocType(DocumentType docType) { this.docType = docType; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public LocalDateTime getUploadTimestamp() { return uploadTimestamp; }
    public boolean isValidated() { return isValidated; }
    public void setValidated(boolean validated) { isValidated = validated; }
}