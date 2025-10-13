package com.tss.aml.service.impl;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tss.aml.dto.request.CreateAccountRequest;
import com.tss.aml.entity.Account;
import com.tss.aml.entity.Customer;
import com.tss.aml.entity.enums.AccountType;
import com.tss.aml.exception.UserApiException;
import com.tss.aml.repository.AccountRepository;
import com.tss.aml.repository.CustomerRepository;
import com.tss.aml.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public Account createAccount(CreateAccountRequest request, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new UserApiException("Customer not found"));

        if (!AccountType.isValid(request.getAccountType())) {
            throw new IllegalArgumentException("Invalid account type: " + request.getAccountType());
        }

        Account account = new Account();
        account.setAccountType(AccountType.valueOf(request.getAccountType().toUpperCase()));
        account.setCurrency(request.getCurrency().toUpperCase());
        account.setBalance(request.getBalance());
        account.setCustomer(customer);

        // Generate a unique 12-digit account number
        String uniqueAccountNumber = generateUniqueAccountNumber();
        account.setAccountNumber(uniqueAccountNumber);

        return accountRepository.save(account);
    }

    @Override
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    /**
     * Generates a unique 12-digit random account number.
     */
    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = generateRandom12DigitNumber();
        } while (accountRepository.findByAccountNumber(accountNumber) != null);
        return accountNumber;
    }

    /**
     * Generates a random 12-digit number as a String (no prefix, just digits).
     */
    private String generateRandom12DigitNumber() {
        long number = 100000000000L + (Math.abs(RANDOM.nextLong()) % 900000000000L);
        return String.valueOf(number);
    }
}
