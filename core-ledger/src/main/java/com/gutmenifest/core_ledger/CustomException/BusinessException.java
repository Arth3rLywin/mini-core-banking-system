package com.gutmenifest.core_ledger.CustomException;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}