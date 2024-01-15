package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {
    List<Transfer> getTransfers();
    List<Transfer> getTransfersByUserId(int id);
    Transfer getTransferById(int id);
    List<Transfer> getPendingTransferRequestsByUserId(int id);
    Transfer createTransfer(Transfer transfer);
    Transfer updateTransfer(Transfer transfer);

}
