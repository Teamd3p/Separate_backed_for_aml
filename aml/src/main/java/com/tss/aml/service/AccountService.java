package com.tss.aml.service;

<<<<<<< HEAD
=======
import java.util.List;

>>>>>>> 3d7d8a1cd41cfa13dbead653ab5dda9af9c601af
import com.tss.aml.dto.request.CreateAccountRequest;
import com.tss.aml.entity.Account;

public interface AccountService {
    Account createAccount(CreateAccountRequest request, Long customerId);
    Account getAccountByNumber(String accountNumber);
    boolean isAccountOwnedByUser(String accountNumber, Long userId);
<<<<<<< HEAD
=======
    List<Account> getAccountsByCustomerId(Long customerId);
>>>>>>> 3d7d8a1cd41cfa13dbead653ab5dda9af9c601af
}
