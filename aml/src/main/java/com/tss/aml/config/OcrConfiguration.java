package com.tss.aml.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tesseract")
public class OcrConfiguration {
    
    private String datapath = "/usr/share/tesseract-ocr/4.00/tessdata";
    private String language = "eng";
    private int pageSegMode = 1;
    private int ocrEngineMode = 1;
    
    // Getters and setters
    public String getDatapath() {
        return datapath;
    }
    
    public void setDatapath(String datapath) {
        this.datapath = datapath;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public int getPageSegMode() {
        return pageSegMode;
    }
    
    public void setPageSegMode(int pageSegMode) {
        this.pageSegMode = pageSegMode;
    }
    
    public int getOcrEngineMode() {
        return ocrEngineMode;
    }
    
    public void setOcrEngineMode(int ocrEngineMode) {
        this.ocrEngineMode = ocrEngineMode;
    }
}
