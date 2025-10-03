package com.tss.aml.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aml")
public class AmlConfigProperties {
    private int blockingRiskThreshold = 70; // default

    // Getter & Setter
    public int getBlockingRiskThreshold() {
        return blockingRiskThreshold;
    }

    public void setBlockingRiskThreshold(int blockingRiskThreshold) {
        this.blockingRiskThreshold = blockingRiskThreshold;
    }
}