package com.tss.aml.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "suspicious_keywords")
public class SuspiciousKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long keywordId;

    @NotNull
    @Column(unique = true)
    private String word;

    @NotNull
    private String category;
    @NotNull
    private Integer severity;
    private boolean isActive = true;

    public SuspiciousKeyword() {}

    public SuspiciousKeyword(String word, String category, int severity) {
        this.word = word;
        this.category = category;
        this.severity = severity;
    }

    public Long getKeywordId() { return keywordId; }
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getSeverity() { return severity; }
    public void setSeverity(Integer severity) { this.severity = severity; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}