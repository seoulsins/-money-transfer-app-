package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {
    private int userId;
    private String username;
    private int accountId;
    private BigDecimal balance;

    public Account(int userId, String username, int accountId, BigDecimal balance) {
        this.userId = userId;
        this.username = username;
        this.accountId = accountId;
        this.balance = balance;
    }

    public Account() {
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
