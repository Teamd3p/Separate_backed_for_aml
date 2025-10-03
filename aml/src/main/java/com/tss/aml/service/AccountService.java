package com.tss.aml.service;

import com.tss.aml.dto.CreateAccountRequest;
import com.tss.aml.entity.Account;

public interface AccountService {
    Account createAccount(CreateAccountRequest request, Long customerId);
}
