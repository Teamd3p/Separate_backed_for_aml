package com.tss.aml.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import com.tss.aml.entity.CurrencyExchange;
import com.tss.aml.repository.CurrencyExchangeRepository;

@Service
public class CurrencyDataInitializationService implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(CurrencyDataInitializationService.class);
    
    @Autowired
    private CurrencyExchangeRepository currencyExchangeRepository;
    
    @Override
    public void run(String... args) throws Exception {
        initializeCurrencyExchangeRates();
    }
    
    private void initializeCurrencyExchangeRates() {
        logger.info("🏦 Initializing currency exchange rates...");
        
        // Check if data already exists
        if (currencyExchangeRepository.count() > 0) {
            logger.info("✅ Currency exchange rates already exist, skipping initialization");
            return;
        }
        
        List<CurrencyExchange> exchangeRates = Arrays.asList(
            // USD as base currency
            createExchangeRate("USD", "INR", new BigDecimal("83.250000"), new BigDecimal("2.50"), new BigDecimal("5.00"), new BigDecimal("500.00")),
            createExchangeRate("USD", "EUR", new BigDecimal("0.920000"), new BigDecimal("1.50"), new BigDecimal("2.00"), new BigDecimal("200.00")),
            createExchangeRate("USD", "GBP", new BigDecimal("0.790000"), new BigDecimal("1.75"), new BigDecimal("3.00"), new BigDecimal("250.00")),
            createExchangeRate("USD", "JPY", new BigDecimal("149.500000"), new BigDecimal("2.00"), new BigDecimal("10.00"), new BigDecimal("1000.00")),
            createExchangeRate("USD", "AUD", new BigDecimal("1.520000"), new BigDecimal("2.25"), new BigDecimal("5.00"), new BigDecimal("300.00")),
            createExchangeRate("USD", "CAD", new BigDecimal("1.360000"), new BigDecimal("2.00"), new BigDecimal("4.00"), new BigDecimal("250.00")),
            createExchangeRate("USD", "SGD", new BigDecimal("1.350000"), new BigDecimal("2.50"), new BigDecimal("5.00"), new BigDecimal("300.00")),
            
            // INR to other currencies
            createExchangeRate("INR", "USD", new BigDecimal("0.012000"), new BigDecimal("2.50"), new BigDecimal("5.00"), new BigDecimal("500.00")),
            createExchangeRate("INR", "EUR", new BigDecimal("0.011000"), new BigDecimal("2.75"), new BigDecimal("6.00"), new BigDecimal("600.00")),
            createExchangeRate("INR", "GBP", new BigDecimal("0.009500"), new BigDecimal("3.00"), new BigDecimal("7.00"), new BigDecimal("700.00")),
            createExchangeRate("INR", "JPY", new BigDecimal("1.800000"), new BigDecimal("2.50"), new BigDecimal("10.00"), new BigDecimal("1000.00")),
            
            // EUR to other currencies
            createExchangeRate("EUR", "USD", new BigDecimal("1.087000"), new BigDecimal("1.50"), new BigDecimal("2.00"), new BigDecimal("200.00")),
            createExchangeRate("EUR", "INR", new BigDecimal("90.450000"), new BigDecimal("2.75"), new BigDecimal("6.00"), new BigDecimal("600.00")),
            createExchangeRate("EUR", "GBP", new BigDecimal("0.860000"), new BigDecimal("1.25"), new BigDecimal("2.50"), new BigDecimal("150.00")),
            createExchangeRate("EUR", "JPY", new BigDecimal("162.500000"), new BigDecimal("2.00"), new BigDecimal("10.00"), new BigDecimal("1000.00")),
            
            // GBP to other currencies
            createExchangeRate("GBP", "USD", new BigDecimal("1.265000"), new BigDecimal("1.75"), new BigDecimal("3.00"), new BigDecimal("250.00")),
            createExchangeRate("GBP", "INR", new BigDecimal("105.300000"), new BigDecimal("3.00"), new BigDecimal("7.00"), new BigDecimal("700.00")),
            createExchangeRate("GBP", "EUR", new BigDecimal("1.163000"), new BigDecimal("1.25"), new BigDecimal("2.50"), new BigDecimal("150.00")),
            createExchangeRate("GBP", "JPY", new BigDecimal("189.200000"), new BigDecimal("2.25"), new BigDecimal("12.00"), new BigDecimal("1200.00")),
            
            // JPY to other currencies
            createExchangeRate("JPY", "USD", new BigDecimal("0.006700"), new BigDecimal("2.00"), new BigDecimal("10.00"), new BigDecimal("1000.00")),
            createExchangeRate("JPY", "INR", new BigDecimal("0.556000"), new BigDecimal("2.50"), new BigDecimal("10.00"), new BigDecimal("1000.00")),
            createExchangeRate("JPY", "EUR", new BigDecimal("0.006150"), new BigDecimal("2.00"), new BigDecimal("10.00"), new BigDecimal("1000.00")),
            createExchangeRate("JPY", "GBP", new BigDecimal("0.005280"), new BigDecimal("2.25"), new BigDecimal("12.00"), new BigDecimal("1200.00"))
        );
        
        currencyExchangeRepository.saveAll(exchangeRates);
        
        logger.info("✅ Successfully initialized {} currency exchange rates", exchangeRates.size());
        logger.info("💰 Supported currency pairs:");
        exchangeRates.forEach(rate -> 
            logger.info("   {} → {} (Rate: {}, Fee: {}%)", 
                rate.getFromCurrency(), 
                rate.getToCurrency(), 
                rate.getConversionRate(), 
                rate.getConversionFeePercent())
        );
    }
    
    private CurrencyExchange createExchangeRate(String fromCurrency, String toCurrency, 
                                               BigDecimal rate, BigDecimal feePercent, 
                                               BigDecimal minFee, BigDecimal maxFee) {
        CurrencyExchange exchange = new CurrencyExchange();
        exchange.setFromCurrency(fromCurrency);
        exchange.setToCurrency(toCurrency);
        exchange.setConversionRate(rate);
        exchange.setConversionFeePercent(feePercent);
        exchange.setMinimumFee(minFee);
        exchange.setMaximumFee(maxFee);
        exchange.setIsActive(true);
        exchange.setRateSource("MANUAL");
        exchange.setLastUpdated(LocalDateTime.now());
        exchange.setCreatedAt(LocalDateTime.now());
        return exchange;
    }
}
