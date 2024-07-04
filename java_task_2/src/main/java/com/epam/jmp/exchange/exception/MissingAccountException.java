package com.epam.jmp.exchange.exception;

public class MissingAccountException extends Exception {
    public MissingAccountException(String accountNumber) {
        super(String.format("Account %s not found!", accountNumber));
    }
}
