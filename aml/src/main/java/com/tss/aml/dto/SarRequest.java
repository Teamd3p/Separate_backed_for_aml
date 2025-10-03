package com.tss.aml.dto;

import com.tss.aml.entity.RiskLevel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SarRequest {
    private String summary;
}