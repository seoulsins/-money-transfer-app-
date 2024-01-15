package com.techelevator.tenmo.exception;

public class TransferException extends IllegalArgumentException{
    public TransferException() {
        super();
    }
    public TransferException(String message) {
        super(message);
    }
    public TransferException(String message, Exception cause) {
        super(message, cause);
    }
}
