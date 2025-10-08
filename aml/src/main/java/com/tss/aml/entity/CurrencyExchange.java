package com.tss.aml.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "currency_exchange")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CurrencyExchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 3)
    private String fromCurrency;

    @Column(nullable = false, length = 3)
    private String toCurrency;

    @Column(nullable = false, precision = 15, scale = 6)
    private BigDecimal conversionRate;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal conversionFeePercent;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minimumFee;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal maximumFee;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private String rateSource = "MANUAL"; // MANUAL, API, BANK

    private LocalDateTime lastUpdated = LocalDateTime.now();
    private LocalDateTime createdAt = LocalDateTime.now();

    // Helper method to create currency pair key
    public String getCurrencyPair() {
        return fromCurrency + "/" + toCurrency;
    }
}
