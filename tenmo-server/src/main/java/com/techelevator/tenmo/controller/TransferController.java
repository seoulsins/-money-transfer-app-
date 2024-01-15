package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.exception.TransferException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/transfers")
public class TransferController {

    private final TransferDao transferDao;
    private final UserDao userDao;
    private final AccountDao accountDao;

    private final int PENDING_STATUS_ID = 1;
    private final int ACCEPTED_STATUS_ID = 2;
    private final int REJECTED_STATUS_ID = 3;
    private final String PENDING = "pending";

    public TransferController(TransferDao transferDao, UserDao userDao, AccountDao accountDao) {
        this.transferDao = transferDao;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @GetMapping("/mytransfers")
    List<Transfer> getUserTransfers(@RequestParam(defaultValue = "") String status_is, Principal principal){
        User currentUser = userDao.getUserByUsername(principal.getName());
        if (status_is.equalsIgnoreCase(PENDING)){
            return transferDao.getPendingTransferRequestsByUserId(currentUser.getId());
        }
        return transferDao.getTransfersByUserId(currentUser.getId());
    }

    @GetMapping("/{id}")
    Transfer getById(@PathVariable int id){
        return transferDao.getTransferById(id);
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    Transfer create(@Valid @RequestBody Transfer transfer){
        Transfer createdTransfer = null;
        try {
            createdTransfer = transferDao.createTransfer(transfer);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if (createdTransfer != null && transfer.getStatusId() == ACCEPTED_STATUS_ID) {
            try {
                processTransfer(transfer);
            } catch (TransferException e){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal transfer amount");
            }
        }
        return createdTransfer;
    }
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("/{id}")
    Transfer update(@Valid @RequestBody Transfer transfer, @PathVariable int id){
        transfer.setTransferId(id);
        Transfer updatedTransfer = null;

        Transfer originalTransfer = transferDao.getTransferById(id);

        try {
            updatedTransfer = transferDao.updateTransfer(transfer);
            if (updatedTransfer != null && originalTransfer.getStatusId() == PENDING_STATUS_ID && updatedTransfer.getStatusId() == ACCEPTED_STATUS_ID){
                processTransfer(transfer);
            }
        } catch (DaoException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found.");
        } catch (TransferException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal transfer amount");
        }
        return updatedTransfer;
    }

    private void processTransfer(Transfer transfer){
        Account fromAccount = accountDao.getAccountById(transfer.getAccountFromId());
        Account toAccount = accountDao.getAccountById(transfer.getAccountToId());
        BigDecimal transferAmount = transfer.getAmount();

        fromAccount.setBalance(fromAccount.getBalance().subtract(transferAmount));
        toAccount.setBalance(toAccount.getBalance().add(transferAmount));

        if (fromAccount.getBalance().compareTo(BigDecimal.ZERO) > 0){
            accountDao.updateAccountBalance(fromAccount);
            accountDao.updateAccountBalance(toAccount);
        } else {
            throw new TransferException();
        }
    }
}
