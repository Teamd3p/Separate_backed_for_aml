package com.tss.aml.service.impl;

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

    @Override
    public Account createAccount(CreateAccountRequest request, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new UserApiException("Customer not found"));

        Account account = new Account();
        
        if(!AccountType.isValid(request.getAccountType())) {
        	throw new IllegalArgumentException("Invalid account type: "+ request.getAccountType());
        }
        account.setAccountType(AccountType.valueOf(request.getAccountType().toUpperCase()));
       
        account.setCurrency(request.getCurrency().toUpperCase());
        
        account.setBalance(request.getBalance());
        
        account.setCustomer(customer);
        
        // Set temporary account number to satisfy database NOT NULL constraint
        account.setAccountNumber("TEMP_" + System.currentTimeMillis());
        
        // Save account first to get auto-generated ID
        Account savedAccount = accountRepository.save(account);

        // Generate proper account number using the saved account ID
        String generatedAccountNumber = String.format("ACC%05d", savedAccount.getAccountId());
        savedAccount.setAccountNumber(generatedAccountNumber);

        // Save again with the proper account number
        return accountRepository.save(savedAccount);
    }

    @Override
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }
}
