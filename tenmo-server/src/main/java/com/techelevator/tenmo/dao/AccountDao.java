package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.util.List;

public interface AccountDao {
    List<Account> getAccounts();
    Account getAccountByUsername(String username);
    Account getAccountById(int id);
    Account getAccountByUserId(int id);
    Account updateAccountBalance(Account updatedAccount);

}
