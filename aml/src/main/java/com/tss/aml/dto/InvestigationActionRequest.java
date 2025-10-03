package com.tss.aml.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestigationActionRequest {
    private String decision; // "TRUE_POSITIVE", "FALSE_POSITIVE"
    private String notes;
    private String sarSummary;
    // Optional SAR summary
}