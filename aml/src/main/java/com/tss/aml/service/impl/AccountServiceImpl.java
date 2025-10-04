package com.tss.aml.service.impl;

import java.util.UUID;

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
        account.setAccountType(AccountType.valueOf(request.getAccountType()));
        account.setCurrency(request.getCurrency());
        account.setBalance(request.getBalance());
        account.setCustomer(customer);
        account.setAccountNumber(generateAccountNumber());

        return accountRepository.save(account);
    }

    @Override
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    private String generateAccountNumber() {
        // A simple way to generate a unique account number
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
    }
}
