package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {
    private int transferId;
    private int typeId;
    private String typeDescription;
    private int statusId;
    private String statusDescription;
    private int accountFromId;
    private int accountToId;
    private BigDecimal amount;
    private String toUsername;
    private String fromUsername;


    public Transfer() {
    }

    public Transfer(int transferId, int typeId, String typeDescription, int statusId, String statusDescription,
                    int accountFromId, int accountToId, BigDecimal amount) {
        this.transferId = transferId;
        this.typeId = typeId;
        this.typeDescription = typeDescription;
        this.statusId = statusId;
        this.statusDescription = statusDescription;
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amount = amount;
    }

    public Transfer(int accountFromId, int accountToId, BigDecimal amount, int typeId, int statusId) {
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amount = amount;
        this.typeId = typeId;
        this.statusId = statusId;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public int getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(int accountFromId) {
        this.accountFromId = accountFromId;
    }

    public int getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(int accountToId) {
        this.accountToId = accountToId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


}
