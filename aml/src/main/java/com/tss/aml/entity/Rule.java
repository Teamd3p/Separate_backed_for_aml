package com.tss.aml.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "rules")
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    @NotNull
    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull
    private RuleType type;

    @NotNull
    @Lob
    private String conditions;

    @NotNull
    private Integer riskScoreImpact;
    private boolean isActive = true;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private Admin updatedBy;

    public Rule() {}

    public Rule(String name, RuleType type, String conditions, int riskScoreImpact) {
        this.name = name;
        this.type = type;
        this.conditions = conditions;
        this.riskScoreImpact = riskScoreImpact;
    }

    public Long getRuleId() { return ruleId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public RuleType getType() { return type; }
    public void setType(RuleType type) { this.type = type; }
    public String getConditions() { return conditions; }
    public void setConditions(String conditions) { this.conditions = conditions; }
    public Integer getRiskScoreImpact() { return riskScoreImpact; }
    public void setRiskScoreImpact(Integer riskScoreImpact) { this.riskScoreImpact = riskScoreImpact; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Admin getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Admin updatedBy) { this.updatedBy = updatedBy; }
}