package com.n26.test.transaction.exception;

/**
 * Created by ghegde on 5/6/17.
 */
public class TransactionRunTimeException extends RuntimeException{
    public TransactionRunTimeException(String message) {
        super(message);
    }
}
