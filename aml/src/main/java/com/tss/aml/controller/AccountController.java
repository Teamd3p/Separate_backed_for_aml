package com.tss.aml.controller;

import com.tss.aml.dto.CreateAccountRequest;
import com.tss.aml.entity.Account;
import com.tss.aml.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/customer/{customerId}")
    public ResponseEntity<Account> createAccount(
            @PathVariable Long customerId,
            @Valid @RequestBody CreateAccountRequest request) {
        Account createdAccount = accountService.createAccount(request, customerId);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }
}
