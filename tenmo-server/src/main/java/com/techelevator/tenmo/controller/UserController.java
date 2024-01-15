package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/accounts")
public class UserController {
    UserDao userDao;

    AccountDao accountDao;
    public UserController(UserDao userDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @GetMapping()
    List<Account> getAccounts(){
        return accountDao.getAccounts();
    }

    @GetMapping("/myaccount")
    Account getActiveAccount(Principal principal){
        Account account = accountDao.getAccountById(Integer.parseInt(principal.getName()));

        if (account == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found", null);
        }

        return account;
    }

    @GetMapping("/{id}")
    Account getAccountByUserId(@PathVariable int id){
        Account account = accountDao.getAccountByUserId(id);

        if (account == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found", null);
        }

        return account;
    }
}
